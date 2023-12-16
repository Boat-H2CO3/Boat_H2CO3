
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Loader;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HomeAdapterListUser extends RecyclerView.Adapter<HomeAdapterListUser.ViewHolder> {

    private final Context context;
    private final List<UserBean> list;
    private int selectedPosition;
    private final boolean hasFooter;

    private final H2CO3MainActivity activity;

    public HomeAdapterListUser(H2CO3MainActivity activity, List<UserBean> list) {
        this.context = activity;
        this.activity = activity;
        this.list = list;
        this.selectedPosition = -1;
        this.hasFooter = true;

        // 加载用户头像并保存到UserBean对象中
        for (UserBean user : list) {
            Drawable userIcon = H2CO3Loader.getHeadDrawable(activity, user.getSkinTexture());
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
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            final UserBean user = list.get(position);
            if (user.isSelected()) {
                selectedPosition = position;
                updateUserState(user);
            }
            holder.nameTextView.setText(user.getUserName());
            holder.stateTextView.setText(getUserStateText(user));
            holder.userIcon.setImageDrawable(getUserIcon(user));

            holder.selectorCardView.setOnClickListener(v -> {
                selectedPosition = holder.getBindingAdapterPosition();
                try {
                    updateSelectedUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                notifyItemChanged(holder.getBindingAdapterPosition()); // 更新特定位置的项
                activity.popView.dismiss();
                updateUserState(user);
            });

            holder.removeImageButton.setOnClickListener(v -> removeUser(holder.getBindingAdapterPosition()));
        } else {
            // FooterView
            holder.addCardView.setOnClickListener(v -> activity.showLoginDialog());
        }
    }

    private void removeUser(int position) {
        AtomicReference<UserBean> userBean = new AtomicReference<>(list.get(position));
        String title = context.getString(org.koishi.launcher.h2co3.resources.R.string.title_delete);
        String message = context.getString(org.koishi.launcher.h2co3.resources.R.string.message_delete) + userBean.get().getUserName();
        String buttonOk = context.getString(org.koishi.launcher.h2co3.resources.R.string.button_ok);
        String buttonCancel = context.getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonOk, (dialog, which) -> {
                    activity.popView.dismiss();
                    UserBean removedUser = list.remove(position);
                    if (position == selectedPosition) {
                        selectedPosition = -1;
                        resetUserState();
                    } else if (position < selectedPosition) {
                        selectedPosition--;
                        userBean.set(list.get(selectedPosition));
                        updateUserState(userBean.get());
                    }
                    notifyItemRemoved(position);
                    try {
                        JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
                        usersJson.remove(removedUser.getUserName());
                        H2CO3Auth.setUserJson(usersJson.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                })
                .setNegativeButton(buttonCancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateUserState(UserBean user) {
        setUserState(user);
        activity.homeTopbarUserName.setText(user.getUserName());
        activity.homeTopbarUserState.setText(getUserStateText(user));
        activity.homeTopbarUserIcon.setImageDrawable(getUserIcon(user));
    }

    /**
     * 更新选中的用户信息
     */
    private void updateSelectedUser() throws JSONException {
        // 获取选中的用户
        UserBean selectedUser = list.get(selectedPosition);
        JSONObject usersJson = new JSONObject(H2CO3Auth.getUserJson());
        try {
            int listSize = list.size();
            // 遍历用户列表
            for (int i = 0; i < listSize; i++) {
                UserBean user = list.get(i);
                // 如果是选中的用户
                if (i == selectedPosition) {
                    // 设置选中用户的登录状态为true
                    selectedUser.setIsSelected(true);
                    usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, true);
                } else {
                    // 设置用户的登录状态为false
                    user.setIsSelected(false);
                    usersJson.getJSONObject(user.getUserName()).put(H2CO3Tools.LOGIN_IS_SELECTED, false);
                }
            }
            // 更新用户信息到登录用户信息的JSON对象中
            usersJson.put(selectedUser.getUserName(), usersJson.getJSONObject(selectedUser.getUserName()));
            // 将更新后的登录用户信息保存到本地
            H2CO3Auth.setUserJson(usersJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            case "1" -> context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
            case "2" -> context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other) + user.getApiUrl();
            default -> context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);
        };
    }

    private Drawable getUserIcon(UserBean user) {
        return user.getIsOffline() ? ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user) : user.getUserIcon();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView stateTextView;
        H2CO3CardView selectorCardView ,addCardView;
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