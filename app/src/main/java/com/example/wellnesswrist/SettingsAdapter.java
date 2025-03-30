package com.example.wellnesswrist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SETTING = 0;
    private static final int TYPE_RESET = 1;

    private List<SettingItem> settingItems;
    private Context context;
    private OnResetClickListener resetClickListener;
    private OnSwitchChangeListener switchChangeListener;

    public interface OnResetClickListener {
        void onResetClick();
    }

    public interface OnSwitchChangeListener {
        void onSwitchChanged(int position, boolean isChecked);
    }

    public SettingsAdapter(Context context, List<SettingItem> settingItems,
                           OnResetClickListener resetClickListener,
                           OnSwitchChangeListener switchChangeListener) {
        this.context = context;
        this.settingItems = settingItems;
        this.resetClickListener = resetClickListener;
        this.switchChangeListener = switchChangeListener;
    }

    @Override
    public int getItemViewType(int position) {
        return settingItems.get(position).isResetButton() ? TYPE_RESET : TYPE_SETTING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SETTING) {
            View view = LayoutInflater.from(context).inflate(R.layout.settings_item, parent, false);
            return new SettingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.settings_reset_item, parent, false);
            return new ResetViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SETTING) {
            SettingViewHolder settingHolder = (SettingViewHolder) holder;
            SettingItem item = settingItems.get(position);
            settingHolder.label.setText(item.getLabel());
            settingHolder.toggleSwitch.setChecked(item.isEnabled());
            settingHolder.toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setEnabled(isChecked);
                switchChangeListener.onSwitchChanged(position, isChecked);
            });
        } else {
            ResetViewHolder resetHolder = (ResetViewHolder) holder;
            resetHolder.resetButton.setOnClickListener(v -> resetClickListener.onResetClick());
        }
    }

    @Override
    public int getItemCount() {
        return settingItems.size();
    }

    static class SettingViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        Switch toggleSwitch;

        SettingViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.setting_label);
            toggleSwitch = itemView.findViewById(R.id.setting_switch);
        }
    }

    static class ResetViewHolder extends RecyclerView.ViewHolder {
        Button resetButton;

        ResetViewHolder(View itemView) {
            super(itemView);
            resetButton = itemView.findViewById(R.id.reset_height_weight_button);
        }
    }
}