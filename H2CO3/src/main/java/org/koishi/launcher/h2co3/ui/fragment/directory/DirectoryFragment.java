package org.koishi.launcher.h2co3.ui.fragment.directory;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.MINECRAFT_DIR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.BaseRecycleAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.data.DbDao;
import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.ui.VanillaActivity;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class DirectoryFragment extends H2CO3Fragment {

    private final String h2co3Dir = MINECRAFT_DIR;
    VersionRecyclerAdapter versionRecyclerAdapter;
    private MaterialAlertDialogBuilder mDialog;
    private LinearLayout page;
    private FloatingActionButton dir, ver;
    private SearchDirAdapter mAdapter;
    private String getH2CO3Dir;
    private DbDao mDbDao;
    @SuppressLint("HandlerLeak")
    private final Handler han = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0 -> mDialog.create().dismiss();
                case 1 -> {
                    mDialog.create().dismiss();
                    mDbDao.insertData(getH2CO3Dir);
                    mAdapter.updata(mDbDao.queryData(""));
                    Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                case 2 ->
                        Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
            }
        }
    };
    private RecyclerView mRecyclerView, mVerRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_directory, container, false);
        page = root.findViewById(R.id.dir_layout);

        H2CO3Tools.loadPaths(requireContext());

        dir = root.findViewById(R.id.ver_new_dir);
        dir.setOnClickListener(v -> showDirDialog());
        ver = root.findViewById(R.id.ver_new_ver);
        ver.setOnClickListener(v -> startActivity(new Intent(requireActivity(), VanillaActivity.class)));
        mRecyclerView = root.findViewById(R.id.mRecyclerView);
        mVerRecyclerView = root.findViewById(R.id.mVerRecyclerView);
        dir.hide();
        initViews();
        initVer();
        //private TabItem rb1,rb2;
        TabLayout tab = root.findViewById(R.id.ver_tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mVerRecyclerView.setVisibility(View.VISIBLE);
                    dir.hide();
                    ver.show();
                }
                if (tab.getPosition() == 1) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mVerRecyclerView.setVisibility(View.GONE);
                    ver.hide();
                    dir.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return root;
    }

    public void initViews() {
        mDbDao = new DbDao(requireActivity(), "Dir.db");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new SearchDirAdapter(mDbDao.queryData(""), requireActivity());
        mAdapter.setRvItemOnclickListener(position -> {
            mDbDao.delete(mDbDao.queryData("").get(position));
            mAdapter.updata(mDbDao.queryData(""));
        });
        if (!mDbDao.hasData(h2co3Dir)) {
            mDbDao.insertData(h2co3Dir);
            mAdapter.updata(mDbDao.queryData(""));
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initVer() {
        File versionlist = new File(H2CO3GameHelper.getGameDirectory() + "/versions");
        if (versionlist.isDirectory() && versionlist.exists()) {
            Comparator<Object> cp = Collator.getInstance(Locale.CHINA);
            String[] getVer = versionlist.list();
            List<String> verList = Arrays.asList(Objects.requireNonNull(getVer));  //此集合无法操作添加元素
            verList.sort(cp);
            mVerRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));//设置布局管理
            versionRecyclerAdapter = new VersionRecyclerAdapter(requireActivity(), verList);
            mVerRecyclerView.setAdapter(versionRecyclerAdapter);
        } else {
            mVerRecyclerView.setAdapter(null);
        }
    }

    public void showDirDialog() {
        mDialog = new MaterialAlertDialogBuilder(requireActivity());
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog_directory, null);
        mDialog.setView(dialogView);
        mDialog.setTitle(org.koishi.launcher.h2co3.resources.R.string.add_directory);

        MaterialButton cancel = dialogView.findViewById(R.id.custom_dir_cancel);
        MaterialButton start = dialogView.findViewById(R.id.custom_dir_ok);
        TextInputLayout lay = dialogView.findViewById(R.id.dialog_dir_lay);
        lay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
        start.setEnabled(false);
        TextInputEditText et = dialogView.findViewById(R.id.dialog_dir_name);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void afterTextChanged(Editable p1) {
                String value = Objects.requireNonNull(et.getText()).toString();
                if (value.matches("(/storage/emulated/0|/sdcard|/mnt/sdcard).*")) {
                    lay.setErrorEnabled(false);
                    start.setEnabled(true);
                } else {
                    lay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
                    start.setEnabled(false);
                }
            }
        });

        AlertDialog dialog = mDialog.create();

        cancel.setOnClickListener(v -> dialog.dismiss());
        start.setOnClickListener(v -> {
            if (Objects.requireNonNull(et.getText()).toString().trim().length() != 0) {
                boolean hasData = mDbDao.hasData(et.getText().toString().trim());
                if (!hasData) {
                    File f = new File(et.getText().toString().trim());
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            getH2CO3Dir = et.getText().toString();
                            newDir();
                        } else {
                            Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_dir), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        getH2CO3Dir = et.getText().toString();
                        newDir();
                    }
                } else {
                    Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_already_exists), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                mAdapter.updata(mDbDao.queryData(""));
            } else {
                Snackbar.make(page, "Please input", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public void newDir() {
        new Thread(() -> {
            try {
                AssetsUtils.extractZipFromAssets(requireActivity(), "pack.zip", getH2CO3Dir);
                han.sendEmptyMessage(1);
            } catch (IOException e) {
                Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_right_dir) + e, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                han.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentDir = H2CO3GameHelper.getGameDirectory();
        File f = new File(currentDir);
        if (f.exists() && f.isDirectory()) {
            initVer();
        } else {
            setDir(h2co3Dir);
            Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mDbDao.delete(currentDir);
            mAdapter.updata(mDbDao.queryData(""));
            initVer();
        }
    }

    public void setDir(String dir) {
        H2CO3GameHelper.setGameDirectory(dir);
        H2CO3GameHelper.setGameAssets(dir + "/assets/virtual/legacy");
        H2CO3GameHelper.setGameAssetsRoot(dir + "/assets");
        H2CO3GameHelper.setGameCurrentVersion(dir + "/versions");
    }

    class SearchDirAdapter extends BaseRecycleAdapter<String> {
        public SearchDirAdapter(List<String> datas, Context mContext) {
            super(datas, mContext);
        }

        @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
        @Override
        protected void bindData(BaseViewHolder holder, final int position) {

            TextView textView = (TextView) holder.getView(R.id.tv_record);
            TextView textView1 = (TextView) holder.getView(R.id.tv_name);
            MaterialCardView lay = (MaterialCardView) holder.getView(R.id.ver_item);
            ImageView check = (ImageView) holder.getView(R.id.ver_check_icon);
            MaterialButton del = (MaterialButton) holder.getView(R.id.tv_remove_dir);
            MaterialButton delDir = (MaterialButton) holder.getView(R.id.tv_del_dir);
            textView.setText(datas.get(position));
            if (datas.get(position).equals(H2CO3GameHelper.getGameDirectory())) {
                lay.setStrokeWidth(11);
                lay.setStrokeColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                lay.setStrokeWidth(0);
            }
            if (datas.get(position).equals(h2co3Dir)) {
                del.setVisibility(View.GONE);
                delDir.setVisibility(View.GONE);
            } else {
                del.setVisibility(View.VISIBLE);
                delDir.setVisibility(View.VISIBLE);
            }

            String str1 = textView.getText().toString();
            str1 = str1.substring(0, str1.lastIndexOf("/"));
            int idx = str1.lastIndexOf("/");
            str1 = str1.substring(idx + 1).toUpperCase();
            textView1.setText(str1);

            File f = new File(textView.getText().toString());
            if (f.isDirectory() && f.exists()) {

            } else {
                check.setImageDrawable(getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
                delDir.setVisibility(View.VISIBLE);
            }
            lay.setOnClickListener(v -> {
                if (f.exists() && f.isDirectory()) {
                    setDir(textView.getText().toString());
                    mAdapter.updata(mDbDao.queryData(""));
                    mVerRecyclerView.setAdapter(null);
                    initVer();
                } else {
                    if (null != mRvItemOnclickListener) {
                        mRvItemOnclickListener.RvItemOnclick(position);
                        mAdapter.updata(mDbDao.queryData(""));
                        Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mVerRecyclerView.setAdapter(null);
                    }
                }

            });
            //
            del.setOnClickListener(view -> {

                if (null != mRvItemOnclickListener) {
                    mRvItemOnclickListener.RvItemOnclick(position);
                }
            });

            delDir.setOnClickListener(view -> {
                if (null != mRvItemOnclickListener) {
                    if (datas.get(position).equals(H2CO3GameHelper.getGameDirectory())) {
                        setDir(h2co3Dir);
                    }
                    //添加"Yes"按钮
                    //添加"Yes"按钮
                    AlertDialog alertDialog1 = new MaterialAlertDialogBuilder(requireActivity())
                            .setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action))//标题
                            .setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del)
                            .setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                                File f1 = new File(datas.get(position));
                                //TODO
                                mRvItemOnclickListener.RvItemOnclick(position);
                                mAdapter.updata(mDbDao.queryData(""));
                                new Thread(() -> {
                                    //String file2= "/data/data/org.koishi.launcher.h2co3/app_runtime";
                                    deleteDirWihtFile(f1);
                                }).start();

                            })
                            .setNegativeButton("No No No", (dialogInterface, i) -> {
                                //TODO
                            })
                            .create();

                    alertDialog1.show();
                }
            });

        }


        public void deleteDirWihtFile(File dir) {
            if (dir == null || !dir.exists() || !dir.isDirectory())
                return;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleteDirWihtFile(file); // 递规的方式删除文件夹
            }
            dir.delete();// 删除目录本身
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_dir;
        }

        public void setDir(String dir) {
            H2CO3GameHelper.setGameDirectory(dir);
            H2CO3GameHelper.setGameAssets(dir + "/assets/virtual/legacy");
            H2CO3GameHelper.setGameAssetsRoot(dir + "/assets");
            H2CO3GameHelper.setGameCurrentVersion(dir + "/versions");
        }
    }


    class VersionRecyclerAdapter extends RecyclerView.Adapter<VersionRecyclerAdapter.MyViewHolder> {
        private final List<String> datas;
        private final LayoutInflater inflater;

        public VersionRecyclerAdapter(Context context, List<String> datas) {
            inflater = LayoutInflater.from(context);
            this.datas = datas;
        }

        //创建每一行的View 用RecyclerView.ViewHolder包装
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View itemView = inflater.inflate(R.layout.item_version_local, null);
            return new MyViewHolder(itemView);
        }

        //给每一行View填充数据
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.textview.setText(datas.get(position));
            File f = new File(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
            String verF = H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position);
            if (verF.equals(H2CO3GameHelper.getGameCurrentVersion())) {
                holder.rl.setStrokeWidth(11);
                holder.rl.setStrokeColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.rl.setStrokeWidth(0);
            }
            if (f.isDirectory() && f.exists()) {
            } else {
                holder.rl.setEnabled(false);
                holder.ic.setImageDrawable(getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
            holder.rl.setOnClickListener(v -> {
                holder.dirs = datas.get(position);
                showExecDialog(holder.dirs);
            });

            if (holder.rl.getTag() == null) {
                holder.rl.setTag(true);
                holder.rl.setOnClickListener(v -> {
                    if (f.exists() && f.isDirectory()) {
                        versionRecyclerAdapter.notifyDataSetChanged();
                        H2CO3GameHelper.setGameCurrentVersion(verF);
                        mVerRecyclerView.setAdapter(versionRecyclerAdapter);
                        if (verF.equals(H2CO3GameHelper.getGameCurrentVersion())) {
                            holder.rl.setStrokeWidth(15);
                            holder.rl.setStrokeColor(getResources().getColor(android.R.color.darker_gray));
                            holder.rl.setElevation(5);
                        } else {
                            holder.rl.setStrokeWidth(0);
                        }
                    }
                });
            }

            holder.btn.setOnClickListener(v -> {
                //添加"Yes"按钮
                //添加"Yes"按钮
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                alertDialogBuilder.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action));
                alertDialogBuilder.setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del);
                alertDialogBuilder.setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                    holder.btn.setVisibility(View.INVISIBLE);
                    holder.textview.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    holder.rl.setEnabled(false);
                    File f1 = new File(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
                    new Thread(() -> {
                        if (f1.isDirectory()) {
                            deleteDirWihtFile(f1);
                        } else {
                            deleteFile(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
                        }
                        han.sendEmptyMessage(2);
                    }).start();
                });
                alertDialogBuilder.setNegativeButton("No No No", (dialogInterface, i) -> {
                });
                AlertDialog alertDialog1 = alertDialogBuilder.create();
                alertDialog1.show();
            });
        }

        public void showExecDialog(String dir) {
            /*MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
            View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog_choose_exec, null);
            dialogBuilder.setView(dialogView);
            // 设置对话框的其他内容
            String load = CHTools.getAppCfg("allVerLoad", "false");
            String loadDir;
            if (load.equals("true")) {
                loadDir = H2CO3GameHelper.getGameDirectory() + "/versions/" + dir;
            } else {
                loadDir = H2CO3GameHelper.getGameDirectory();
            }
            LinearLayout lay = dialogView.findViewById(R.id.ver_exec_mod);
            lay.setOnClickListener(v -> {
                Intent i = new Intent(requireActivity(), ModsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mod", loadDir);
                i.putExtras(bundle);
                requireActivity().startActivity(i);
            });

            AlertDialog dialog = dialogBuilder.create();

            WindowManager windowManager = requireActivity().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth() * 0.9);
            dialog.getWindow().setAttributes(lp);

            dialog.show();*/
        }

        //数据源的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void deleteDirWihtFile(File dir) {
            if (dir == null || !dir.exists() || !dir.isDirectory())
                return;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleteDirWihtFile(file); // 递规的方式删除文件夹
            }
            dir.delete();// 删除目录本身
        }

        public void deleteFile(String filePath) {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private final TextView textview;
            private final MaterialButton btn;
            private final ImageView ic;
            private final MaterialCardView rl;
            private String dirs;

            public MyViewHolder(View itemView) {
                super(itemView);
                textview = itemView.findViewById(R.id.ver_name);
                btn = itemView.findViewById(R.id.ver_remove);
                rl = itemView.findViewById(R.id.ver_item);
                ic = itemView.findViewById(R.id.ver_icon);
            }
        }
    }
}