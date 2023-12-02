package org.koishi.launcher.h2co3.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.HomeAdapterListUser;
import org.koishi.launcher.h2co3.application.H2CO3Application;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.H2CO3Auth;
import org.koishi.launcher.h2co3.core.login.H2CO3Loader;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.core.login.other.AuthResult;
import org.koishi.launcher.h2co3.core.login.other.LoginUtils;
import org.koishi.launcher.h2co3.core.login.other.Servers;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3ProgressDialog;
import org.koishi.launcher.h2co3.resources.component.popwindow.H2CO3PopupWindow;
import org.koishi.launcher.h2co3.utils.HomeLoginHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import timber.log.Timber;

public class H2CO3MainActivity extends H2CO3Activity implements View.OnClickListener {
    private static final int MICROSOFT_LOGIN_REQUEST_CODE = 1001;

    public H2CO3TextView homeTopbarUserName;
    public H2CO3TextView homeTopbarUserState;
    public AppCompatImageView homeTopbarUserIcon;
    private LinearLayoutCompat homeTopbarUser;
    public H2CO3PopupWindow popView;
    private HomeAdapterListUser adapterUser;
    private final List<UserBean> userList = H2CO3Auth.getUserList();

    private H2CO3ToolBar toolbar;
    private CircularRevealFrameLayout loginNameLayout;
    private TextInputEditText loginName, loginPassword;
    private ConstraintLayout loginApi;
    private TextInputLayout loginPasswordLayout;
    private H2CO3Button login;
    private H2CO3CustomViewDialog loginDialog, otherLoginDialog;
    private H2CO3MessageDialog microsoftsoftLoginWaitDialog;
    public AlertDialog loginDialogAlert;
    public AlertDialog microsoftsoftLoginWaitDialogAlert;
    private AlertDialog otherLoginWaitDialogAlert;
    private NavController navController;

    private final Gson GLOBAL_GSON = new GsonBuilder().setPrettyPrinting().create();

    private H2CO3ProgressDialog progressDialog;
    private Spinner serverSpinner;
    private EditText userEditText;
    private EditText passEditText;
    private Button loginButton;
    private TextView register;
    private ImageButton addServer;
    private File serversFile;
    private Servers servers;
    private List<String> serverList;
    private String currentBaseUrl;
    private String currentRegisterUrl;
    private ArrayAdapter<String> serverSpinnerAdapter;

    private MaterialAlertDialogBuilder alertDialogBuilder;

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

        String usersJson = H2CO3Auth.getUserJson();
        if (TextUtils.isEmpty(usersJson) || usersJson.equals("{}")) {
            setDefaultUserState();
        } else {
            setUserStateFromJson();
        }
    }

    private void setDefaultUserState() {
        homeTopbarUserName.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        homeTopbarUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(H2CO3MainActivity.this, org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }

    @SuppressLint("SetTextI18n")
    private void setUserStateFromJson() {
        String apiUrl = H2CO3Tools.getBoatValueString(H2CO3Tools.LOGIN_API_URL, H2CO3Tools.LOGIN_ERROR);
        homeTopbarUserName.setText(H2CO3Tools.getBoatValueString(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, ""));
        String userType = H2CO3Tools.getBoatValueString(H2CO3Tools.LOGIN_USER_TYPE, "0");
        String userSkinTexture = H2CO3Tools.getBoatValueString(H2CO3Tools.LOGIN_USER_SKINTEXTURE, "");

        final String MICROSOFT_USER_STATE = getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
        final String OTHER_USER_STATE = getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other);
        final String OFFLINE_USER_STATE = getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);

        switch (userType) {
            case "1" -> {
                homeTopbarUserState.setText(MICROSOFT_USER_STATE);
                H2CO3Loader.getHead(this, userSkinTexture, homeTopbarUserIcon);
            }
            case "2" -> {
                homeTopbarUserState.setText(OTHER_USER_STATE + apiUrl);
                H2CO3Loader.getHead(this, userSkinTexture, homeTopbarUserIcon);
            }
            default -> {
                homeTopbarUserState.setText(OFFLINE_USER_STATE);
                homeTopbarUserIcon.setImageDrawable(ContextCompat.getDrawable(this, org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == homeTopbarUser) {
            try {
                showUserListPopup();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showUserListPopup() throws IOException {
        popView = new H2CO3PopupWindow.Builder(this)
                .setView(R.layout.layout_user_list)
                .setViewOnClickListener((view, layoutResId) -> {
                    userList.clear();
                    H2CO3Auth.parseJsonToUser();
                    adapterUser = new HomeAdapterListUser(this, userList);
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
                .setBackGroundLevel(0.1f)
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
        loginApi = loginDialog.findViewById(R.id.server_selector);
        loginNameLayout = loginDialog.findViewById(R.id.login_name_layout);
        loginPasswordLayout = loginDialog.findViewById(R.id.login_password_layout);
        login = loginDialog.findViewById(R.id.login);
        serversFile = new File(H2CO3Tools.H2CO3_SETTING_DIR, "h2co3_login_servers.json");
        progressDialog = new H2CO3ProgressDialog(this);
        progressDialog.setCancelable(false);

        serverSpinner = loginDialog.findViewById(R.id.server_spinner);
        register = loginDialog.findViewById(R.id.register);
        addServer = loginDialog.findViewById(R.id.add_server);
        TabLayout tab = loginDialog.findViewById(R.id.login_tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loginNameLayout.setVisibility(View.VISIBLE);
                    loginPasswordLayout.setVisibility(View.GONE);
                    loginApi.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 1) {
                    loginNameLayout.setVisibility(View.GONE);
                    loginPasswordLayout.setVisibility(View.GONE);
                    loginApi.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 2) {
                    loginNameLayout.setVisibility(View.VISIBLE);
                    loginPasswordLayout.setVisibility(View.VISIBLE);
                    loginApi.setVisibility(View.VISIBLE);
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
            int selectedTabPosition = tab.getSelectedTabPosition();
            if (selectedTabPosition == 0) {
                if (isValidUsername(text)) {
                    H2CO3Auth.addUserToJson(text, "", "", "0", "", "", "", "", "", "", "", true, false);
                    adapterUser.notifyDataSetChanged();
                    popView.dismiss();
                    loginDialogAlert.dismiss();
                }
            } else if (selectedTabPosition == 1) {
                startActivityForResult(new Intent(this, MicrosoftLoginActivity.class), MICROSOFT_LOGIN_REQUEST_CODE);
            } else if (selectedTabPosition == 2) {
                progressDialog.showWithProgress();
                H2CO3Application.sExecutorService.execute(() -> {
                    String user = loginName.getText().toString();
                    String pass = Objects.requireNonNull(loginPassword.getText()).toString();
                    if (!user.isEmpty() && !pass.isEmpty()) {
                        try {
                            LoginUtils.getINSTANCE().setBaseUrl(currentBaseUrl);
                            LoginUtils.getINSTANCE().login(user, pass, new LoginUtils.Listener() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    runOnUiThread(() -> {
                                        progressDialog.dismiss();
                                        if (authResult.getSelectedProfile() != null) {
                                            H2CO3Auth.addUserToJson(authResult.getSelectedProfile().getName(), text, pass, "2", currentBaseUrl, authResult.getSelectedProfile().getId(), "", "", authResult.getAccessToken(), "", "", true, false);
                                            adapterUser.notifyDataSetChanged();
                                            popView.dismiss();
                                            loginDialogAlert.dismiss();
                                        } else {
                                            String[] items = authResult.getAvailableProfiles().stream()
                                                    .map(AuthResult.AvailableProfiles::getName).toArray(String[]::new);
                                            alertDialogBuilder = new MaterialAlertDialogBuilder(H2CO3MainActivity.this);
                                            alertDialogBuilder.setTitle("请选择角色");
                                            alertDialogBuilder.setItems(items, (dialog, which) -> {
                                                AuthResult.AvailableProfiles selectedProfile = authResult.getAvailableProfiles().get(which);
                                                H2CO3Auth.addUserToJson(selectedProfile.getName(), text, pass, "2", currentBaseUrl, selectedProfile.getId(), "", "", authResult.getAccessToken(), "", "", true, false);
                                                adapterUser.notifyDataSetChanged();
                                                popView.dismiss();
                                                loginDialogAlert.dismiss();
                                            });
                                            alertDialogBuilder.setNegativeButton(H2CO3MainActivity.this.getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
                                            alertDialogBuilder.show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(String error) {
                                    runOnUiThread(() -> {
                                        progressDialog.dismiss();
                                        adapterUser.notifyDataSetChanged();
                                        popView.dismiss();
                                        loginDialogAlert.dismiss();
                                        alertDialogBuilder = new MaterialAlertDialogBuilder(H2CO3MainActivity.this);
                                        alertDialogBuilder.setTitle(org.koishi.launcher.h2co3.resources.R.string.title_warn);
                                        alertDialogBuilder.setMessage(error);
                                        alertDialogBuilder.setPositiveButton(H2CO3MainActivity.this.getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), null);
                                        alertDialogBuilder.show();
                                    });
                                }
                            });
                        } catch (IOException e) {
                            runOnUiThread(progressDialog::dismiss);
                            Timber.tag("登录").e(e.toString());
                        }
                    }
                });
            }
        });

        refreshServer();
        serverSpinner.setAdapter(serverSpinnerAdapter);
        addServer.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("请选择认证服务器类型");
            alertDialogBuilder.setItems(new String[]{"外置登录", "统一通行证"}, (dialog, which) -> {
                EditText editText = new EditText(this);
                editText.setMaxLines(1);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                MaterialAlertDialogBuilder inputDialogBuilder = new MaterialAlertDialogBuilder(this);
                inputDialogBuilder.setTitle("提示");
                inputDialogBuilder.setView(editText);
                inputDialogBuilder.setPositiveButton(this.getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), (dialogInterface, i) -> {
                    progressDialog.showWithProgress();
                    H2CO3Application.sExecutorService.execute(() -> {
                        String baseUrl = which == 0 ? editText.getText().toString() : "https://auth.mc-user.com:233/" + editText.getText().toString();
                        String data = LoginUtils.getINSTANCE().getServeInfo(baseUrl);
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            if (data != null) {
                                try {
                                    Servers.Server server = new Servers.Server();
                                    JSONObject jsonObject = new JSONObject(data);
                                    JSONObject meta = jsonObject.optJSONObject("meta");
                                    if (meta != null) {
                                        server.setServerName(meta.optString("serverName"));
                                    }
                                    server.setBaseUrl(editText.getText().toString());
                                    if (which == 0) {
                                        JSONObject links = null;
                                        if (meta != null) {
                                            links = meta.optJSONObject("links");
                                        }
                                        if (links != null) {
                                            server.setRegister(links.optString("register"));
                                        }
                                    } else {
                                        server.setBaseUrl("https://auth.mc-user.com:233/" + editText.getText().toString());
                                        server.setRegister("https://login.mc-user.com:233/" + editText.getText().toString() + "/loginreg");
                                    }
                                    Optional.ofNullable(servers)
                                            .orElseGet(() -> {
                                                servers = new Servers();
                                                servers.setServer(new ArrayList<>());
                                                return servers;
                                            });
                                    servers.getServer().add(server);
                                    H2CO3Tools.write(serversFile.getAbsolutePath(), GLOBAL_GSON.toJson(servers, Servers.class));
                                    refreshServer();
                                    currentBaseUrl = server.getBaseUrl();
                                    currentRegisterUrl = server.getRegister();
                                } catch (Exception ignored) {
                                }
                            }
                        });
                    });
                });
                inputDialogBuilder.setNegativeButton(this.getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
                inputDialogBuilder.show();
            });
            alertDialogBuilder.setNegativeButton(this.getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
            alertDialogBuilder.show();
        });

        register.setOnClickListener(v -> Optional.ofNullable(currentRegisterUrl)
                .ifPresent(url -> {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(currentRegisterUrl);
                    intent.setData(uri);
                    startActivity(intent);
                }));
    }

    public void refreshServer() {
        List<String> serverList = new ArrayList<>();
        if (serversFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(serversFile))) {
                servers = new Gson().fromJson(reader, Servers.class);
                if (servers != null) {
                    currentBaseUrl = servers.getServer().get(0).getBaseUrl();
                    for (Servers.Server server : servers.getServer()) {
                        serverList.add(server.getServerName());
                    }
                }
            } catch (IOException ignored) {
            }
        }
        if (servers == null) {
            serverList.add("无认证服务器");
        }
        if (serverSpinnerAdapter == null) {
            serverSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, serverList);
        } else {
            serverSpinnerAdapter.notifyDataSetChanged();
        }
    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username)
                && username.length() >= 3
                && username.length() <= 16
                && username.matches("\\w+");
    }

    private final HomeLoginHandler loginHandler = new HomeLoginHandler(this);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROSOFT_LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            microsoftsoftLoginWaitDialog = new H2CO3MessageDialog(H2CO3MainActivity.this);
            microsoftsoftLoginWaitDialog.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.login_wait));
            microsoftsoftLoginWaitDialog.setMessage(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.login_wait));
            microsoftsoftLoginWaitDialogAlert = microsoftsoftLoginWaitDialog.create();
            microsoftsoftLoginWaitDialogAlert.setCancelable(false);
            microsoftsoftLoginWaitDialogAlert.show();
            if (loginHandler != null) {
                loginHandler.login(data);
            }
        }
    }
}