package org.koishi.launcher.h2co3.dialog.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.dialog.list.events.ListItemClick;

import java.util.ArrayList;

public class DefaultListAdapter extends RecyclerView.Adapter<DefaultListAdapterGeneric.ViewHolder> {

    String[] items;
    boolean isSelectable;
    ListItemClick itemClick;
    ArrayList<String> selectedItems;
    int itemBgRes;
    int itemBgResSelected;
    int listItemBgColor;
    int listItemBgColorSelected;
    int textColor;
    int selected = -1;

    public DefaultListAdapter(String[] items,
                              boolean isSelectable,
                              ListItemClick itemClick,
                              ArrayList<String> selectedItems,
                              int itemBgRes,
                              int itemBgResSelected,
                              int textColor,
                              int listItemBgColor,
                              int listItemBgColorSelected) {
        this.items = items;
        this.isSelectable = isSelectable;
        this.itemClick = itemClick;
        this.selectedItems = selectedItems;
        this.itemBgRes = itemBgRes;
        this.itemBgResSelected = itemBgResSelected;
        this.textColor = textColor;
        this.listItemBgColor = listItemBgColor;
        this.listItemBgColorSelected = listItemBgColorSelected;

    }

    @NonNull
    @Override
    public DefaultListAdapterGeneric.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_list_item, parent, false);
        return new DefaultListAdapterGeneric.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DefaultListAdapterGeneric.ViewHolder holder, int position) {
        holder.getVal1Txt().setText(items[position]);

        if (selectedItems.contains(items[position])) {
            setItemResource(holder, itemBgResSelected);
            setItemColor(holder, listItemBgColorSelected);
        } else {
            setItemResource(holder, itemBgRes);
            setItemColor(holder, listItemBgColor);
        }
        if (textColor != 1)
            holder.getVal1Txt().setTextColor(textColor);

        if (isSelectable) {
            holder.getView().setOnClickListener(v -> {
                if (!selectedItems.contains(items[position]))
                    selectItem(holder, position);
                else deselectItem(holder, position);
            });
            return;
        }

        if (selected != -1) {
            if (selected == position)
                selectItem(holder, position);
            else
                deselectItem(holder, position);
        }

        holder.getView().setOnClickListener(v -> itemClick.onClick(position, items[position]));

    }

    private void selectItem(@NonNull DefaultListAdapterGeneric.ViewHolder holder, int position) {
        selectedItems.add(items[position]);
        setItemResource(holder, itemBgResSelected);
        setItemColor(holder, listItemBgColorSelected);

    }

    public void selectItem(int id) {
        selected = id;
    }

    public int getSelectedItem() {
        return selected;
    }

    private void deselectItem(@NonNull DefaultListAdapterGeneric.ViewHolder holder, int position) {
        selectedItems.remove(items[position]);
        setItemResource(holder, itemBgRes);
        setItemColor(holder, listItemBgColor);
    }

    private void setItemResource(@NonNull DefaultListAdapterGeneric.ViewHolder holder, int drawable) {
        holder.getView().setBackgroundResource(drawable);
    }

    private void setItemColor(@NonNull DefaultListAdapterGeneric.ViewHolder holder, int color) {
        if (color == -1)
            return;
        holder.getView().getBackground().mutate().setTint(color);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

}
