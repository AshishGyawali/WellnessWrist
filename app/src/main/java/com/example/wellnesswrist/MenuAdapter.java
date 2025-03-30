package com.example.wellnesswrist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private List<MenuItem> menuItems;
    private Context context;
    private OnItemClickListener itemClickListener;
    private int highlightedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MenuAdapter(Context context, List<MenuItem> menuItems, OnItemClickListener itemClickListener) {
        this.context = context;
        this.menuItems = menuItems;
        this.itemClickListener = itemClickListener;
    }

    public void setHighlightedPosition(int position) {
        this.highlightedPosition = position;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.label.setText(item.getLabel());
        holder.icon.setImageResource(item.getIconResId());

        // Highlight the center item
        holder.itemView.setSelected(position == highlightedPosition);

        // Increase text size for the selected item
        if (position == highlightedPosition) {
            holder.label.setTextSize(16); // Larger text size for selected item
        } else {
            holder.label.setTextSize(14); // Default text size for unselected items
        }

        // Restrict clicks to the center item
        holder.itemView.setOnClickListener(v -> {
            if (position == highlightedPosition) {
                itemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView icon;

        MenuViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.menuLabel);
            icon = itemView.findViewById(R.id.menuIcon);
        }
    }
}