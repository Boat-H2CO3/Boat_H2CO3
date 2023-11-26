package org.koishi.launcher.h2co3.adapter;

import static org.koishi.launcher.h2co3.core.login.H2CO3Auth.setUserState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.H2CO3Loader;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.ui.H2CO3MainActivity;

import java.util.List;

public class AdapterListUser extends BaseAdapter {

    private final Context context;
    private final List<UserBean> list;
    private int selectedPosition;
    private String userJson;

    private final H2CO3MainActivity activity;

    public AdapterListUser(H2CO3MainActivity activity, List<UserBean> list) {
        this.context = activity;
        this.activity = activity;
        this.list = list;
        this.selectedPosition = -1;

        // 加载用户头像并保存到UserBean对象中
        for (UserBean user : list) {
            Drawable userIcon = H2CO3Loader.getHeadDrawable(activity, user.getSkinTexture());
            user.setUserIcon(userIcon);
        }
    }

    public AdapterListUser(Context context, List<UserBean> list, String usersJson, H2CO3MainActivity activity) {
        this.context = activity;
        this.list = list;
        this.activity = activity;
        this.selectedPosition = -1;
        this.userJson = usersJson;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.item_listview_user_name);
            holder.stateTextView = convertView.findViewById(R.id.item_listview_user_state);
            holder.selectorCardView = convertView.findViewById(R.id.login_user_item);
            holder.userIcon = convertView.findViewById(R.id.item_listview_userImageView);
            holder.removeImageButton = convertView.findViewById(R.id.item_listview_user_remove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserBean user = list.get(position);
        if (user.isSelected()) {
            selectedPosition = position;
            updateUserState(user);
        }
        holder.nameTextView.setText(user.getUserName());
        holder.stateTextView.setText(getUserStateText(user));
        holder.userIcon.setImageDrawable(getUserIcon(user));

        holder.selectorCardView.setOnClickListener(v -> {
            selectedPosition = position;
            updateSelectedUser();
            notifyDataSetChanged();
            activity.popView.dismiss();
            updateUserState(user);
        });

        holder.removeImageButton.setOnClickListener(v -> removeUser(position));

        return convertView;
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
    private void updateSelectedUser() {
        // 获取选中的用户
        UserBean selectedUser = list.get(selectedPosition);
        JSONObject usersJson;
        try {
            // 获取登录用户信息的JSON对象
            usersJson = new JSONObject(H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USERS, ""));
            // 获取选中用户的JSON对象
            JSONObject selectedUserJson = usersJson.getJSONObject(selectedUser.getUserName());
            // 设置选中用户的登录状态为true
            selectedUserJson.put(H2CO3Tools.LOGIN_IS_SELECTED, true);
            // 更新用户信息到登录用户信息的JSON对象中
            usersJson.put(selectedUser.getUserName(), selectedUserJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // 遍历用户列表
        for (int i = 0; i < list.size(); i++) {
            UserBean user = list.get(i);
            // 如果不是选中的用户
            if (i != selectedPosition) {
                try {
                    // 获取用户的JSON对象
                    JSONObject userJson = usersJson.getJSONObject(user.getUserName());
                    // 设置用户的登录状态为false
                    userJson.put(H2CO3Tools.LOGIN_IS_SELECTED, false);
                    // 更新用户信息到登录用户信息的JSON对象中
                    usersJson.put(user.getUserName(), userJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // 设置用户的选中状态
            user.setIsSelected(i == selectedPosition);
        }

        // 将更新后的登录用户信息保存到本地
        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USERS, usersJson.toString());
    }

    private void removeUser(int position) {
        UserBean userBean = list.get(position);
        String title = context.getString(org.koishi.launcher.h2co3.resources.R.string.title_delete);
        String message = context.getString(org.koishi.launcher.h2co3.resources.R.string.message_delete) + userBean.getUserName();
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
                        updateUserState(userBean);
                    }
                    notifyDataSetChanged();
                    try {
                        JSONObject usersJson = new JSONObject(H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USERS, ""));
                        usersJson.remove(removedUser.getUserName());
                        H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USERS, usersJson.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(buttonCancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetUserState() {
        UserBean emptyUser = new UserBean();
        setUserState(emptyUser);
        activity.homeTopbarUserName.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        activity.homeTopbarUserState.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        activity.homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }

    private void loadUserIcon(UserBean user) {
        Drawable userIcon = H2CO3Loader.getHeadDrawable(context, user.getSkinTexture());
        user.setUserIcon(userIcon);
    }

    private String getUserStateText(UserBean user) {
        switch (user.getUserType()) {
            case "1":
                return context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
            case "2":
                return context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other) + user.getApiUrl();
            default:
                return context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);
        }
    }

    private Drawable getUserIcon(UserBean user) {
        return user.getIsOffline() ? ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user) : user.getUserIcon();
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView stateTextView;
        H2CO3CardView selectorCardView;
        ImageButton removeImageButton;
        ImageView userIcon;
    }
}