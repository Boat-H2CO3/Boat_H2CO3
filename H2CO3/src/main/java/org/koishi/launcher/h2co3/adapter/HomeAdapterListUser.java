package org.koishi.launcher.h2co3.adapter;

import static org.koishi.launcher.h2co3.core.H2CO3Auth.setUserState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Loader;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MaterialDialog;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAdapterListUser extends RecyclerView.Adapter<HomeAdapterListUser.ViewHolder> {

    private final Context context;
    private final List<UserBean> list;
    private final boolean hasFooter;
    private final Map<String, Drawable> userIconCache = new HashMap<>();
    private final H2CO3MainActivity activity;
    private int selectedPosition;
    private boolean isRemoveUserDialogShowing = false;

    public HomeAdapterListUser(H2CO3MainActivity activity, List<UserBean> list) {
        this.context = activity;
        this.activity = activity;
        this.list = list;
        this.selectedPosition = -1;
        this.hasFooter = true;

        for (UserBean user : list) {
            Drawable userIcon = getUserIcon(user);
            user.setUserIcon(userIcon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (hasFooter ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < list.size()) {
            return 0; // 用户项
        } else {
            return 1; // Footer项
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        if (viewType == 0) {
            itemView = inflater.inflate(R.layout.item_user_list, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.item_user_add, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int viewType = holder.getItemViewType();
        if (viewType == 0) {
            final UserBean user = list.get(position);
            if (user.isSelected()) {
                selectedPosition = position;
                updateUserState(user);
                holder.selectorCardView.setStrokeWidth(13);
                holder.selectorCardView.setClickable(false); // 禁用点击
                holder.selectorCardView.setOnClickListener(null); // 移除点击事件监听器
            } else {
                holder.selectorCardView.setStrokeWidth(0);
                holder.selectorCardView.setClickable(true); // 启用点击
                holder.selectorCardView.setOnClickListener(v -> {
                    selectedPosition = holder.getBindingAdapterPosition();
                    try {
                        updateSelectedUser();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    notifyItemChanged(holder.getBindingAdapterPosition());
                    activity.popView.dismiss();
                    updateUserState(user);
                });
            }
            holder.nameTextView.setText(user.getUserName());
            holder.stateTextView.setText(getUserStateText(user));

            if (user.getUserIcon() == null) {
                Drawable userIcon = getUserIcon(user);
                user.setUserIcon(userIcon);
            }
            holder.userIcon.setImageDrawable(user.getUserIcon());

            holder.removeImageButton.setOnClickListener(v -> {
                if (!isRemoveUserDialogShowing) {
                    isRemoveUserDialogShowing = true;
                    showRemoveUserDialog(holder.getBindingAdapterPosition());
                }
            });
        } else {
            holder.addCardView.setOnClickListener(v -> activity.showLoginDialog());
        }
    }

    private Drawable getUserIcon(UserBean user) {
        if (user.getIsOffline()) {
            return ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user);
        } else {
            Drawable cachedIcon = userIconCache.get(user.getUserName());
            if (cachedIcon != null) {
                return cachedIcon;
            } else {
                Drawable userIcon = H2CO3Loader.getHeadDrawable(activity, user.getSkinTexture());
                userIconCache.put(user.getUserName(), userIcon);
                return userIcon;
            }
        }
    }

    private void updateSelectedUser() throws JSONException {
        UserBean selectedUser = list.get(selectedPosition);
        JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
        try {
            for (UserBean user : list) {
                user.setIsSelected(user == selectedUser);
                usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, user == selectedUser);
            }
            usersJson.put(selectedUser.getUserName(), usersJson.getJSONObject(selectedUser.getUserName()));
            H2CO3Auth.setUserJson(usersJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeUser(int position) {
        UserBean removedUser = list.remove(position);
        if (position == selectedPosition) {
            selectedPosition = -1;
            resetUserState();
        } else if (position < selectedPosition) {
            selectedPosition--;
        }
        notifyItemRemoved(position);

        list.removeIf(user -> user == removedUser);

        try {
            JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
            usersJson.remove(removedUser.getUserName());
            H2CO3Auth.setUserJson(usersJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUserState(UserBean user) {
        setUserState(user);
        activity.homeTopbarUserName.setText(user.getUserName());
        activity.homeTopbarUserState.setText(getUserStateText(user));
        activity.homeTopbarUserIcon.setImageDrawable(getUserIcon(user));
    }

    private void resetUserState() {
        UserBean emptyUser = new UserBean();
        setUserState(emptyUser);
        activity.homeTopbarUserName.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        activity.homeTopbarUserState.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        activity.homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }

    private String getUserStateText(UserBean user) {
        return switch (user.getUserType()) {
            case "1" ->
                    context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
            case "2" ->
                    context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other) + user.getApiUrl();
            default ->
                    context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);
        };
    }

    private void showRemoveUserDialog(int position) {
        H2CO3MaterialDialog dialog = new H2CO3MaterialDialog(context);
        dialog.setTitle("确认删除用户");
        dialog.setMessage("确定要删除该用户吗？");
        dialog.setPositiveButton("确定", (dialogInterface, which) -> {
            removeUser(position);
            isRemoveUserDialogShowing = false;
            activity.popView.dismiss();
        });
        dialog.setNegativeButton("取消", (dialogInterface, which) -> {
            isRemoveUserDialogShowing = false;
        });
        dialog.setOnDismissListener(dialogInterface -> {
            isRemoveUserDialogShowing = false;
        });
        dialog.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView stateTextView;
        H2CO3CardView selectorCardView, addCardView;
        ImageButton removeImageButton;
        ImageView userIcon;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_listview_user_name);
            stateTextView = itemView.findViewById(R.id.item_listview_user_state);
            selectorCardView = itemView.findViewById(R.id.login_user_item);
            userIcon = itemView.findViewById(R.id.item_listview_userImageView);
            removeImageButton = itemView.findViewById(R.id.item_listview_user_remove);
            addCardView = itemView.findViewById(R.id.login_user_add);
        }
    }
}