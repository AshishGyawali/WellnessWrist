package com.example.wellnesswrist;

public class SettingItem {
    private String label;
    private boolean isEnabled;
    private boolean isResetButton;

    public SettingItem(String label, boolean isEnabled, boolean isResetButton) {
        this.label = label;
        this.isEnabled = isEnabled;
        this.isResetButton = isResetButton;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isResetButton() {
        return isResetButton;
    }
}