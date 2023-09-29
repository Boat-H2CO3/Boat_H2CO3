package org.koishi.launcher.h2co3.dialog.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.dialog.ImageListItem;
import org.koishi.launcher.h2co3.dialog.list.events.ListItemClickObj;

import java.util.ArrayList;

public class DefaultImageListAdapter extends RecyclerView.Adapter<DefaultImageListAdapter.ViewHolder> {

    public ArrayList<ImageListItem> arrayListItems;

    boolean isSelectable;

    int itemBgRes;
    int itemBgResSelected;
    int listItemBgColor;
    int listItemBgColorSelected;
    int textColor;
    int selected = -1;

    ListItemClickObj<ImageListItem> itemClick;
    ArrayList<ImageListItem> selectedItems;


    public DefaultImageListAdapter(ArrayList<ImageListItem> arrayList,
                                   boolean isSelectable,
                                   @Nullable ListItemClickObj<ImageListItem> itemClick,
                                   ArrayList<ImageListItem> selectedItems,
                                   int itemBgRes,
                                   int itemBgResSelected,
                                   int textColor,
                                   int listItemBgColor,
                                   int listItemBgColorSelected) {
        this.itemClick = itemClick;
        this.isSelectable = isSelectable;
        this.selectedItems = selectedItems;
        this.itemBgRes = itemBgRes;
        this.itemBgResSelected = itemBgResSelected;
        this.textColor = textColor;
        this.arrayListItems = arrayList;
        this.listItemBgColor = listItemBgColor;
        this.listItemBgColorSelected = listItemBgColorSelected;
    }

    @NonNull
    @Override
    public DefaultImageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_image_list_item, parent, false);
        return new DefaultImageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setValues(holder, position);
        setBackground(holder, position);

        if (textColor != 1) {
            holder.getTxt().setTextColor(textColor);
        }

        if (isSelectable) {
            holder.getView().setOnClickListener(v ->
                    checkSelected(holder, arrayListItems.get(position))
            );

            return;
        }

        if (selected != -1) {
            if (selected == position)
                selectItem(holder, arrayListItems.get(position));
            else
                deselectItem(holder, arrayListItems.get(position));
        }

        holder.getView().setOnClickListener(v -> itemClick.onClick(position, arrayListItems.get(position)));

    }

    private void setBackground(@NonNull ViewHolder holder, int position) {
        if (!isSelectable) {
            setItemResource(holder, itemBgRes);
            setItemColor(holder, listItemBgColor);
            return;
        }

        if (selectedItems.contains(arrayListItems.get(position))) {
            setItemResource(holder, itemBgResSelected);
            setItemColor(holder, listItemBgColorSelected);
        } else {
            setItemResource(holder, itemBgRes);
            setItemColor(holder, listItemBgColor);
        }
    }

    private void setValues(@NonNull ViewHolder holder, int position) {
        holder.getTxt().setText(arrayListItems.get(position).getName());
        holder.getImageView().setImageDrawable(arrayListItems.get(position).getImage());

    }

    private void checkSelected(@NonNull ViewHolder holder, ImageListItem obj) {
        if (!selectedItems.contains(obj))
            selectItem(holder, obj);
        else deselectItem(holder, obj);
    }

    private void selectItem(@NonNull ViewHolder holder, ImageListItem obj) {
        selectedItems.add(obj);
        setItemResource(holder, itemBgResSelected);
        setItemColor(holder, listItemBgColorSelected);
    }

    private void deselectItem(@NonNull ViewHolder holder, ImageListItem obj) {
        selectedItems.remove(obj);
        setItemResource(holder, itemBgRes);
        setItemColor(holder, listItemBgColor);
    }

    public void selectItem(int id) {
        selected = id;
    }

    private void setItemResource(@NonNull ViewHolder holder, int drawable) {
        holder.getView().setBackgroundResource(drawable);
    }

    private void setItemColor(@NonNull ViewHolder holder, int color) {
        if (color == -1)
            return;
        holder.getView().getBackground().mutate().setTint(color);
    }

    @Override
    public int getItemCount() {
        return arrayListItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valTxt;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valTxt = itemView.findViewById(R.id.value1Txt);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public TextView getTxt() {
            return valTxt;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public View getView() {
            return itemView.findViewById(R.id.layoutItem);
        }

    }
}
