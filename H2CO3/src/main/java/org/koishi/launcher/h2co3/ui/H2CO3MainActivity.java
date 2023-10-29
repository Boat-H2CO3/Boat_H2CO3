package org.koishi.launcher.h2co3.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.popwindow.CommonPopupWindow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener {

    private static final String USER_STATE_OFFLINE = "user_state_offline";
    private static final String USER_ADD = "user_add";

    private H2CO3TextView homeTopbarUserName, homeTopbarUserState;
    private AppCompatImageView homeTopbarUserIcon;
    private LinearLayoutCompat homeTopbarUser;
    private CommonPopupWindow popView;
    private AdapterListUser adapterUser;
    private List<UserBean> userList;

    private RecyclerView recyclerView;

    private ImageButton dropDown;
    private CircularRevealFrameLayout loginNameLayout;
    private TextInputEditText loginName, loginPassword, loginApi;
    private TextInputLayout loginPasswordLayout, loginApiLayout;
    private H2CO3Button login;
    private H2CO3CustomViewDialog loginDialog;
    private AlertDialog loginDialogAlert;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        initUI();
    }

    private void initUI() {
        // 初始化UI界面
        homeTopbarUserName = findViewById(R.id.home_topbar_user_name);
        homeTopbarUserState = findViewById(R.id.home_topbar_user_state);
        homeTopbarUserIcon = findViewById(R.id.home_topbar_user_icon);
        homeTopbarUser = findViewById(R.id.home_topbar_user);
        homeTopbarUser.setOnClickListener(this);

        // 获取用户信息
        String usersJson = H2CO3Tools.getH2CO3ValueString("users", "");
        if (usersJson.isEmpty() || usersJson.equals("{}")) {
            H2CO3Tools.setBoatValue("auth_player_name", "");
            H2CO3Tools.setBoatValue("apiUrl", "");
            homeTopbarUserName.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
            homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        } else {
            homeTopbarUserName.setText(H2CO3Tools.getBoatValueString("auth_player_name",""));
            if (H2CO3Tools.getBoatValue("isOffline", true)) {
                homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
            } else {
                homeTopbarUserState.setText(H2CO3Tools.getBoatValueString("apiUrl", "Error"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == homeTopbarUser) {
            showUserListPopup();
        }
    }

    private void showUserListPopup() {
        // 显示用户列表弹窗
        popView = new CommonPopupWindow.Builder(this)
                .setView(R.layout.layout_user_list)
                .setViewOnClickListener((view, layoutResId) -> {
                    parseJsonToUser();
                    adapterUser = new AdapterListUser(this, userList);
                    ListView userList = view.findViewById(R.id.layout_user_listview);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View footView = inflater.inflate(R.layout.item_user_add, null, false);
                    userList.addFooterView(footView);
                    H2CO3CardView userAdd = footView.findViewById(R.id.login_user_add);
                    userAdd.setOnClickListener(v1 -> showLoginDialog());
                    userList.setAdapter(adapterUser);
                })
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setBackGroundLevel(0.6f)
                .builder();
        popView.showAsDropDown(homeTopbarUser);
    }

    private void showLoginDialog() {
        // 显示登录对话框
        loginDialog = new H2CO3CustomViewDialog(this);
        loginDialog.setCustomView(R.layout.custom_dialog_login);
        loginDialog.setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.title_activity_login));

        loginDialogAlert = loginDialog.create();
        loginDialogAlert.show();

        loginName = loginDialog.findViewById(R.id.login_name);
        loginPassword = loginDialog.findViewById(R.id.login_password);
        loginApi = loginDialog.findViewById(R.id.login_api);
        loginNameLayout = loginDialog.findViewById(R.id.login_name_layout);
        loginPasswordLayout = loginDialog.findViewById(R.id.login_password_layout);
        loginApiLayout = loginDialog.findViewById(R.id.login_api_layout);
        login = loginDialog.findViewById(R.id.login);
        TabLayout tab = loginDialog.findViewById(R.id.login_tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loginNameLayout.setVisibility(View.VISIBLE);
                    loginPasswordLayout.setVisibility(View.GONE);
                    loginApiLayout.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 1) {
                    loginNameLayout.setVisibility(View.GONE);
                    loginPasswordLayout.setVisibility(View.GONE);
                    loginApiLayout.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 2) {
                    loginNameLayout.setVisibility(View.VISIBLE);
                    loginPasswordLayout.setVisibility(View.VISIBLE);
                    loginApiLayout.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(loginName.getText())) {
                        addUserToJson(loginName.getText().toString(), true, false, "", "", "", "", "");
                        adapterUser.notifyDataSetChanged();
                        popView.dismiss();
                        loginDialogAlert.dismiss();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        login.setOnClickListener(p1 -> {

        });
    }

    private void addUserToJson(String name, Boolean isOffline, boolean isSelected, String apiUrl, String account, String pass, String uuid, String token) {
        // 将用户信息添加到JSON中
        try {
            String usersJson = H2CO3Tools.getH2CO3ValueString("users", "");
            JSONObject json = new JSONObject(usersJson.isEmpty() ? "{}" : usersJson);

            JSONObject userData = new JSONObject();
            userData.put("isOffline", isOffline);
            userData.put("apiUrl", apiUrl);
            userData.put("isSelected", isSelected);
            userData.put("loginInfo", new JSONArray().put(0, account).put(1, pass));
            userData.put("uuid", uuid);
            userData.put("token", token);

            json.put(name, userData);

            H2CO3Tools.setH2CO3Value("users", json.toString());
            parseJsonToUser();
        } catch (JSONException ignored) {
        }
    }

    private void parseJsonToUser() {
        // 解析JSON数据为用户列表
        userList = new ArrayList<>();
        String usersJson = H2CO3Tools.getH2CO3ValueString("users", "");
        if (usersJson.isEmpty() || usersJson.equals("{}")) {
            return;
        }

        try {
            JSONObject usersObj = new JSONObject(usersJson);
            Iterator<String> keys = usersObj.keys();
            while (keys.hasNext()) {
                String userName = keys.next();
                JSONObject userObj = usersObj.getJSONObject(userName);

                UserBean user = new UserBean();
                user.setUserName(userName);
                user.setIsOffline(userObj.getBoolean("isOffline"));
                user.setApiUrl(userObj.getString("apiUrl"));
                user.setIsSelected(userObj.getBoolean("isSelected"));
                user.setUuid(userObj.getString("uuid"));
                user.setToken(userObj.getString("token"));

                JSONArray loginInfoArray = userObj.getJSONArray("loginInfo");
                if (loginInfoArray.length() >= 2) {
                    user.setUserAccount(loginInfoArray.getString(0));
                    user.setUserPass(loginInfoArray.getString(1));
                }

                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class AdapterListUser extends BaseAdapter {

        private final Context context;
        private final List<UserBean> list;
        private int selectedPosition;

        public AdapterListUser(Context context, List<UserBean> list) {
            this.context = context;
            this.list = list;
            this.selectedPosition = -1;
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false);
                holder = new ViewHolder();
                holder.nameTextView = convertView.findViewById(R.id.item_listview_user_name);
                holder.stateTextView = convertView.findViewById(R.id.item_listview_user_state);
                holder.selectorCardView = convertView.findViewById(R.id.login_user_item);
                holder.removeImageButton = convertView.findViewById(R.id.item_listview_user_remove);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final UserBean user = list.get(position);
            if (user.isSelected()) {
                selectedPosition = position;
                homeTopbarUserName.setText(user.getUserName());
                if (user.getIsOffline()) {
                    homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
                    H2CO3Tools.setBoatValue("isOffline", true);
                } else {
                    homeTopbarUserState.setText(user.getApiUrl());
                    H2CO3Tools.setBoatValue("isOffline", false);
                }
            }
            holder.nameTextView.setText(user.getUserName());
            if (user.getIsOffline()) {
                holder.stateTextView.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
            } else {
                holder.stateTextView.setText(user.getApiUrl());
            }
            holder.selectorCardView.setOnClickListener(null);
            holder.selectorCardView.setOnClickListener(v -> {
                selectedPosition = position;
                updateSelectedUser();
                notifyDataSetChanged();
                popView.dismiss();
                H2CO3Tools.setBoatValue("auth_player_name", user.getUserName());
                homeTopbarUserName.setText(user.getUserName());
                if (user.getIsOffline()) {
                    H2CO3Tools.setBoatValue("user_type", 0);
                    homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
                } else {
                    H2CO3Tools.setBoatValue("user_type", 0);
                    homeTopbarUserState.setText(user.getApiUrl());
                }
            });

            holder.removeImageButton.setOnClickListener(v -> {
                removeUser(position);
            });

            return convertView;
        }

        private void updateSelectedUser() {
            // 更新选中的用户
            UserBean selectedUser = list.get(selectedPosition);

            try {
                JSONObject usersJson = new JSONObject(H2CO3Tools.getH2CO3ValueString("users", ""));
                JSONObject selectedUserJson = usersJson.getJSONObject(selectedUser.getUserName());
                selectedUserJson.put("isSelected", true);
                usersJson.put(selectedUser.getUserName(), selectedUserJson);

                for (int i = 0; i < list.size(); i++) {
                    UserBean user = list.get(i);
                    if (i != selectedPosition) {
                        JSONObject userJson = usersJson.getJSONObject(user.getUserName());
                        userJson.put("isSelected", false);
                        usersJson.put(user.getUserName(), userJson);
                    }
                    user.setIsSelected(i == selectedPosition);
                }

                H2CO3Tools.setH2CO3Value("users", usersJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void removeUser(int position) {
            // 移除用户
            UserBean removedUser = list.remove(position);
            popView.dismiss();
            if (position == selectedPosition) {
                selectedPosition = -1;
                H2CO3Tools.setBoatValue("auth_player_name", "");
                H2CO3Tools.setBoatValue("apiUrl", "");
                homeTopbarUserName.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
                homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
            } else if (position < selectedPosition) {
                selectedPosition--;
                UserBean userBean = list.get(selectedPosition);
                H2CO3Tools.setBoatValue("auth_player_name",userBean.getUserName());
                H2CO3Tools.setBoatValue("apiUrl", userBean.getApiUrl());
                homeTopbarUserName.setText(H2CO3Tools.getBoatValueString("auth_player_name",userBean.getUserName()));
                if (H2CO3Tools.getBoatValue("isOffline", true)) {
                    homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
                } else {
                    homeTopbarUserState.setText(H2CO3Tools.getBoatValueString("apiUrl", userBean.getApiUrl()));
                }
            }
            notifyDataSetChanged();

            try {
                JSONObject usersJson = new JSONObject(H2CO3Tools.getH2CO3ValueString("users", ""));
                usersJson.remove(removedUser.getUserName());
                H2CO3Tools.setH2CO3Value("users", usersJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private static class ViewHolder {
            TextView nameTextView;
            TextView stateTextView;
            H2CO3CardView selectorCardView;
            ImageButton removeImageButton;
        }
    }
}