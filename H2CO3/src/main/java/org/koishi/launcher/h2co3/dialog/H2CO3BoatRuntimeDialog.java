package org.koishi.launcher.h2co3.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.BaseRecycleAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Game;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.data.DbDao;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.ui.fragment.directory.DirectoryFragment;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class H2CO3BoatRuntimeDialog extends H2CO3CustomViewDialog {
    private final RecyclerView recyclerView;

    public Context context;
    private RuntimeAdapter adapter;

    private DbDao mDbDao;
    public H2CO3BoatRuntimeDialog(Context context) {
        super(context);
        this.context = context;
        setTitle(org.koishi.launcher.h2co3.resources.R.string.title_runtime);
        this.setCustomView(R.layout.custom_dialog_runtime);
        recyclerView = this.findViewById(R.id.recycler_runtime);

        initViews();

        // Custom button behavior without dismiss
    }

    public void initViews() {
        mDbDao = new DbDao(context,"Runtime.db");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<String> queryData = mDbDao.queryData("");
        adapter = new H2CO3BoatRuntimeDialog.RuntimeAdapter(queryData, context);
        adapter.setRvItemOnclickListener(position -> {
            mDbDao.delete(queryData.get(position));
            adapter.updata(queryData);
        });
        if (!mDbDao.hasData(H2CO3Tools.JAVA_8_PATH)) {
            mDbDao.insertData(H2CO3Tools.JAVA_8_PATH);
            adapter.updata(queryData);
        }
        recyclerView.setAdapter(adapter);
    }

    /** Show the dialog, refreshes the adapter data before showing it */
    @SuppressLint("NotifyDataSetChanged") //only used to completely refresh the list, it is necessary
    @Override
    public AlertDialog show() {
        refresh();
        return super.show();
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    class RuntimeAdapter extends BaseRecycleAdapter<String> {
        public RuntimeAdapter(List<String> datas, Context mContext) {
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
            if (datas.get(position).equals(H2CO3Game.getJavaPath())) {
                lay.setStrokeWidth(13);
                lay.setStrokeColor(mContext.getResources().getColor(android.R.color.darker_gray));
            } else {
                lay.setStrokeWidth(0);
            }
            if (datas.get(position).equals(H2CO3Tools.JAVA_8_PATH)) {
                del.setVisibility(View.GONE);
                delDir.setVisibility(View.GONE);
            } else {
                del.setVisibility(View.VISIBLE);
                delDir.setVisibility(View.VISIBLE);
            }

            String str1 = textView.getText().toString();
            int lastSlashIndex = str1.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                str1 = str1.substring(lastSlashIndex + 1).toUpperCase();
            }
            textView1.setText(str1);

            File f = new File(textView.getText().toString());
            if (!f.exists() || !f.isDirectory()) {
                check.setImageDrawable(mContext.getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
                delDir.setVisibility(View.VISIBLE);
            }
            lay.setOnClickListener(v -> {
                if (f.exists() && f.isDirectory()) {
                    setDir(textView.getText().toString());
                    adapter.updata(datas);
                    recyclerView.setAdapter(null);
                    initViews();
                } else {
                    if (null != mRvItemOnclickListener) {
                        mRvItemOnclickListener.RvItemOnclick(position);
                        adapter.updata(datas);
                        recyclerView.setAdapter(null);
                        initViews();
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
                    if (datas.get(position).equals(H2CO3Game.getGameDirectory())) {
                        setDir(H2CO3Tools.JAVA_8_PATH);
                    }
                    //添加"Yes"按钮
                    //添加"Yes"按钮
                    AlertDialog alertDialog1 = new MaterialAlertDialogBuilder(mContext)
                            .setTitle(mContext.getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action))//标题
                            .setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del)
                            .setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                                File f1 = new File(datas.get(position));
                                //TODO
                                mRvItemOnclickListener.RvItemOnclick(position);
                                adapter.updata(datas);
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
            H2CO3Game.setJavaPath(dir);
        }
    }
}