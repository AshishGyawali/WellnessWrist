package com.example.wellnesswrist;

import androidx.annotation.DrawableRes;

public class MenuItem {
    private String label;
    private @DrawableRes int iconResId;

    public MenuItem(String label, @DrawableRes int iconResId) {
        this.label = label;
        this.iconResId = iconResId;
    }

    public String getLabel() {
        return label;
    }

    public int getIconResId() {
        return iconResId;
    }
}