package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.wellnesswrist.databinding.ActivityMenuBinding;
import java.util.Arrays;
import java.util.List;

public class MenuActivity extends ComponentActivity {
    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WearableRecyclerView recyclerView = binding.menuRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEdgeItemsCenteringEnabled(true);

        List<MenuItem> menuItems = Arrays.asList(
                new MenuItem("Exercise", R.drawable.ic_exercise, ExerciseListActivity.class),
                new MenuItem("Caffeine", R.drawable.ic_caffeine, CaffeineInputActivity.class),
                new MenuItem("Breathing", R.drawable.ic_breathing, BreathingActivity.class),
                new MenuItem("Settings", R.drawable.ic_settings, SettingsActivity.class)
        );

        MenuAdapter adapter = new MenuAdapter(menuItems, this::startActivity);
        recyclerView.setAdapter(adapter);
    }
}

class MenuItem {
    String name;
    int iconResId;
    Class<?> targetActivity;

    MenuItem(String name, int iconResId, Class<?> targetActivity) {
        this.name = name;
        this.iconResId = iconResId;
        this.targetActivity = targetActivity;
    }
}

class MenuAdapter extends WearableRecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private final List<MenuItem> menuItems;
    private final IntentStarter intentStarter;

    interface IntentStarter {
        void startActivity(Intent intent);
    }

    MenuAdapter(List<MenuItem> menuItems, IntentStarter intentStarter) {
        this.menuItems = menuItems;
        this.intentStarter = intentStarter;
    }

    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.textView.setText(item.name);
        holder.imageView.setImageResource(item.iconResId);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), item.targetActivity);
            intentStarter.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolder extends WearableRecyclerView.ViewHolder {
        android.widget.TextView textView;
        android.widget.ImageView imageView;

        ViewHolder(android.view.View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.menu_text);
            imageView = itemView.findViewById(R.id.menu_icon);
        }
    }
}