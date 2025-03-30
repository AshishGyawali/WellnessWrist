package com.example.wellnesswrist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.wellnesswrist.databinding.ActivitySettingsBinding;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ComponentActivity {
    private ActivitySettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private List<SettingItem> settingItems;
    private SettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WellnessWristPrefs", MODE_PRIVATE);

        // Initialize settings items
        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem("Monitor Posture", sharedPreferences.getBoolean("monitorPosture", false), false));
        settingItems.add(new SettingItem("Drink Water", sharedPreferences.getBoolean("drinkWater", false), false));
        settingItems.add(new SettingItem("Sunrise Alarm", sharedPreferences.getBoolean("sunriseAlarm", false), false));
        settingItems.add(new SettingItem("Ambient Noise", sharedPreferences.getBoolean("ambientNoise", false), false));
        settingItems.add(new SettingItem("Reset Height and Weight", false, true));

        // Set up the WearableRecyclerView
        WearableRecyclerView recyclerView = binding.settingsRecyclerView;
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        recyclerView.setEdgeItemsCenteringEnabled(true);

        // Set up the adapter
        adapter = new SettingsAdapter(this, settingItems,
                () -> {
                    // Handle reset button click
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("height");
                    editor.remove("weight");
                    editor.apply();
                    Toast.makeText(this, "Height and Weight reset", Toast.LENGTH_SHORT).show();
                },
                (position, isChecked) -> {
                    // Handle switch changes
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    switch (position) {
                        case 0:
                            editor.putBoolean("monitorPosture", isChecked);
                            break;
                        case 1:
                            editor.putBoolean("drinkWater", isChecked);
                            break;
                        case 2:
                            editor.putBoolean("sunriseAlarm", isChecked);
                            break;
                        case 3:
                            editor.putBoolean("ambientNoise", isChecked);
                            break;
                    }
                    editor.apply();
                });
        recyclerView.setAdapter(adapter);
    }
}