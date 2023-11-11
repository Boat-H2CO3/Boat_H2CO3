package org.koishi.launcher.h2co3.ui;

import static org.koishi.launcher.h2co3.core.login.H2CO3Auth.addUserToJson;
import static org.koishi.launcher.h2co3.core.login.H2CO3Auth.parseJsonToUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.H2CO3Auth;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;
import org.koishi.launcher.h2co3.resources.component.popwindow.H2CO3PopupWindow;
import org.koishi.launcher.h2co3.utils.LoginHandler;

import java.util.List;
import java.util.Objects;

public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener {
    private H2CO3TextView homeTopbarUserName, homeTopbarUserState;
    private AppCompatImageView homeTopbarUserIcon;
    private LinearLayoutCompat homeTopbarUser;
    public H2CO3PopupWindow popView;
    public AdapterListUser adapterUser;
    private final List<UserBean> userList = H2CO3Auth.getUserList();

    private H2CO3ToolBar toolbar;
    private CircularRevealFrameLayout loginNameLayout;
    private TextInputEditText loginName, loginPassword, loginApi;
    private TextInputLayout loginPasswordLayout, loginApiLayout;
    private H2CO3Button login;
    private H2CO3CustomViewDialog loginDialog;
    private H2CO3MessageDialog microsoftsoftLoginWaitDialog;
    public AlertDialog loginDialogAlert;
    public AlertDialog microsoftsoftLoginWaitDialogAlert;
    private NavController navController;

    public H2CO3MainActivity() {
    }

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
        homeTopbarUserName = findViewById(R.id.home_topbar_user_name);
        homeTopbarUserState = findViewById(R.id.home_topbar_user_state);
        homeTopbarUserIcon = findViewById(R.id.home_topbar_user_icon);
        homeTopbarUser = findViewById(R.id.home_topbar_user);
        homeTopbarUser.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);

        String usersJson = H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USERS, "");
        if (TextUtils.isEmpty(usersJson) || usersJson.equals("{}")) {
            setDefaultUserState();
        } else {
            setUserStateFromJson(usersJson);
        }
    }

    private void setDefaultUserState() {
        homeTopbarUserName.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(H2CO3MainActivity.this, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }

    private void setUserStateFromJson(String usersJson) {
        try {
            JSONObject json = new JSONObject(usersJson);
            String userName = H2CO3Tools.getBoatValueString(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, "");
            boolean isOffline = H2CO3Tools.getBoatValue(H2CO3Tools.LOGIN_IS_OFFLINE, true);
            String apiUrl = H2CO3Tools.getBoatValueString(H2CO3Tools.LOGIN_API_URL, H2CO3Tools.LOGIN_ERROR);

            homeTopbarUserName.setText(userName);
            if (isOffline) {
                homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
                homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(this, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user));
            } else {
                homeTopbarUserState.setText(apiUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == homeTopbarUser) {
            showUserListPopup();
        }
    }

    private void showUserListPopup() {
        popView = new H2CO3PopupWindow.Builder(this)
                .setView(R.layout.layout_user_list)
                .setViewOnClickListener((view, layoutResId) -> {
                    userList.clear();
                    parseJsonToUser();
                    adapterUser = new AdapterListUser(this, userList);
                    ListView userList = view.findViewById(R.id.layout_user_listview);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    @SuppressLint("InflateParams") View footView = inflater.inflate(R.layout.item_user_add, null, false);
                    userList.addFooterView(footView);
                    H2CO3CardView userAdd = footView.findViewById(R.id.login_user_add);
                    userAdd.setOnClickListener(v1 -> showLoginDialog());
                    userList.setAdapter(adapterUser);
                })
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setBackGroundLevel(0.2f)
                .builder();
        popView.showAsDropDown(toolbar);
    }

    private void showLoginDialog() {
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
            String text = Objects.requireNonNull(loginName.getText()).toString();
            if (tab.getSelectedTabPosition() == 0) {
                if (!TextUtils.isEmpty(text)
                        && text.length() >= 3
                        && text.length() <= 16
                        && text.matches("\\w+")) {
                    addUserToJson(loginName.getText().toString(), "", "", "", "", "", "", "","","","",true,false);
                    adapterUser.notifyDataSetChanged();
                    popView.dismiss();
                    loginDialogAlert.dismiss();
                }
            } else if (tab.getSelectedTabPosition() == 1) {

                startActivityForResult(new Intent(this, MicrosoftLoginActivity.class), MicrosoftLoginActivity.AUTHENTICATE_MICROSOFT_REQUEST);
            }
        });
    }
    private final LoginHandler loginHandler = new LoginHandler(this);
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MicrosoftLoginActivity.AUTHENTICATE_MICROSOFT_REQUEST && resultCode == Activity.RESULT_OK) {
            microsoftsoftLoginWaitDialog = new H2CO3MessageDialog(H2CO3MainActivity.this);
            microsoftsoftLoginWaitDialog.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.login_wait));
            microsoftsoftLoginWaitDialog.setMessage(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.login_wait));
            microsoftsoftLoginWaitDialogAlert = microsoftsoftLoginWaitDialog.create();
            microsoftsoftLoginWaitDialogAlert.setCancelable(false);
            microsoftsoftLoginWaitDialogAlert.show();
            loginHandler.login(data);
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
                updateUserState(user);
            }
            holder.nameTextView.setText(user.getUserName());
            holder.stateTextView.setText(user.getIsOffline() ? context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline) : user.getApiUrl());
            holder.selectorCardView.setOnClickListener(v -> {
                selectedPosition = position;
                updateSelectedUser();
                notifyDataSetChanged();
                popView.dismiss();
                updateUserState(user);
            });

            holder.removeImageButton.setOnClickListener(v -> removeUser(position));

            return convertView;
        }

        private void updateUserState(UserBean user) {
            homeTopbarUserName.setText(user.getUserName());
            H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, user.getUserName());
            if (user.getIsOffline()) {
                homeTopbarUserState.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline));
                homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(H2CO3MainActivity.this, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user));
                H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_IS_OFFLINE, true);
            } else {
                homeTopbarUserState.setText(user.getApiUrl());
                H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_IS_OFFLINE, false);
            }
        }

        private void updateSelectedUser() {
            UserBean selectedUser = list.get(selectedPosition);

            try {
                JSONObject usersJson = new JSONObject(H2CO3Tools.getH2CO3ValueString(H2CO3Tools.LOGIN_USERS, ""));
                JSONObject selectedUserJson = usersJson.getJSONObject(selectedUser.getUserName());
                selectedUserJson.put(H2CO3Tools.LOGIN_IS_SELECTED, true);
                usersJson.put(selectedUser.getUserName(), selectedUserJson);

                for (int i = 0; i < list.size(); i++) {
                    UserBean user = list.get(i);
                    if (i != selectedPosition) {
                        JSONObject userJson = usersJson.getJSONObject(user.getUserName());
                        userJson.put(H2CO3Tools.LOGIN_IS_SELECTED, false);
                        usersJson.put(user.getUserName(), userJson);
                    }
                    user.setIsSelected(i == selectedPosition);
                }

                H2CO3Tools.setH2CO3Value(H2CO3Tools.LOGIN_USERS, usersJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void removeUser(int position) {
            UserBean userBean = list.get(position);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle(context.getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_delete))
                    .setMessage(context.getResources().getString(org.koishi.launcher.h2co3.resources.R.string.message_delete) + userBean.getUserName())
                    .setPositiveButton(context.getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), (dialog, which) -> {
                        popView.dismiss();
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
                    .setNegativeButton(context.getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        private void resetUserState() {
            H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, "");
            H2CO3Tools.setBoatValue(H2CO3Tools.LOGIN_API_URL, "");
            homeTopbarUserName.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
            homeTopbarUserState.setText(context.getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
            homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(H2CO3MainActivity.this, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
        }

        private static class ViewHolder {
            TextView nameTextView;
            TextView stateTextView;
            H2CO3CardView selectorCardView;
            ImageButton removeImageButton;
        }
    }
}