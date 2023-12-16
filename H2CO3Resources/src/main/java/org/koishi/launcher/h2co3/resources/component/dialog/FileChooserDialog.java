package org.koishi.launcher.h2co3.resources.component.dialog;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.resources.component.dialog.adapter.FileAdapter;
import org.koishi.launcher.h2co3.resources.component.dialog.model.ChooserFile;
import org.koishi.launcher.h2co3.resources.component.dialog.model.ChooserStackDirectory;
import org.koishi.launcher.h2co3.resources.component.dialog.model.MarginItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FileChooserDialog implements FileAdapter.OnClickListener {
    private static final String TAG = "FileChooserDialog";

    private final LinkedList<ChooserStackDirectory> directoryStack;
    private final String extension;
    private final OnFileChosenListener listener;
    private final AlertDialog alertDialog;
    private final FileAdapter fileAdapter;

    private final RecyclerView recyclerView;

    private FileChooserDialog(Context context, String title, String startPath, String extension, OnFileChosenListener listener) {
        this.directoryStack = new LinkedList<>();
        if (startPath != null) {
            this.directoryStack.add(new ChooserStackDirectory(new File(startPath)));
        } else {
            this.directoryStack.add(new ChooserStackDirectory(Environment.getExternalStorageDirectory()));
        }
        this.extension = extension;
        this.listener = listener;
        View root = LayoutInflater.from(context).inflate(R.layout.file_chooser_dialog, null);
        this.recyclerView = root.findViewById(R.id.recycler_view);

        this.fileAdapter = new FileAdapter(getFiles(), this);

        recyclerView.setAdapter(fileAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new MarginItemDecoration());

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context)
                .setView(root)
                .setNegativeButton("Cancel", null);
        if (title == null) {
            title = "Select a file";
            if (extension != null) {
                title += " (" + extension + ")";
            }
        }
        alertDialogBuilder.setTitle(title);
        this.alertDialog = alertDialogBuilder.create();
    }

    public void show() {
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    private List<ChooserFile> getFiles() {
        List<ChooserFile> directories = new ArrayList<>();
        List<ChooserFile> files = new ArrayList<>();

        for (File f : Objects.requireNonNull(directoryStack.getLast().getFile().listFiles())) {
            if (f.isDirectory()) {
                directories.add(getChooserFile(f, false));
            } else if (extension == null || f.getName().endsWith(extension)) {
                files.add(getChooserFile(f, false));
            }
        }

        directories.sort((o1, o2) -> o1.name().compareToIgnoreCase(o2.name()));
        files.sort((o1, o2) -> o1.name().compareToIgnoreCase(o2.name()));

        if (directoryStack.size() > 1) {
            directories.add(0, getChooserFile(directoryStack.get(directoryStack.size() - 2).getFile(), true));
        }

        directories.addAll(files);

        return directories;
    }

    private ChooserFile getChooserFile(File file, boolean isRoot) {
        String name = isRoot ? ".." : file.getName();
        String size = "Size: " + file.length() + "B";
        return new ChooserFile(name, size, file);
    }

    @Override
    public void onClick(ChooserFile file) {
        if (file.file().isDirectory()) {
            if ("..".equals(file.name())) {
                directoryStack.removeLast();
            } else {
                directoryStack.getLast().setOffset(recyclerView.computeVerticalScrollOffset());
                directoryStack.addLast(new ChooserStackDirectory(file.file()));
            }
            fileAdapter.setFiles(getFiles());
            recyclerView.scrollBy(0, directoryStack.getLast().getOffset());
        } else {
            listener.onFileChosen(file.file());
            dismiss();
        }
    }

    public interface OnFileChosenListener {
        void onFileChosen(File file);
    }

    public static class Builder {
        private final Context context;
        private final OnFileChosenListener listener;
        private String title;
        private String startPath;
        private String extension;

        public Builder(Context context, OnFileChosenListener listener) {
            this.context = context;
            this.listener = listener;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setStartPath(String startPath) {
            this.startPath = startPath;
            return this;
        }

        public Builder setExtension(String extension) {
            if (extension != null && !extension.startsWith(".")) {
                extension = "." + extension;
            }
            this.extension = extension;
            return this;
        }

        public FileChooserDialog build() {
            return new FileChooserDialog(context, title, startPath, extension, listener);
        }
    }
}