package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.wellnesswrist.databinding.ActivityMenuBinding;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends ComponentActivity {
    private ActivityMenuBinding binding;
    private MenuAdapter adapter;
    private int centerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Exercise", R.drawable.ic_exercise));
        menuItems.add(new MenuItem("Caffeine", R.drawable.ic_caffeine));
        menuItems.add(new MenuItem("Breathing", R.drawable.ic_breathing));
        menuItems.add(new MenuItem("Settings", R.drawable.ic_settings));

        WearableRecyclerView recyclerView = binding.menuRecyclerView;
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this, new CustomLayoutCallback()));
        recyclerView.setCircularScrollingGestureEnabled(true);
        recyclerView.setBezelFraction(0.5f);
        recyclerView.setScrollDegreesPerScreen(90);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        recyclerView.addOnScrollListener(new WearableRecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateChildViews(recyclerView);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateChildViews(recyclerView);
                }
            }

            private void updateChildViews(RecyclerView recyclerView) {
                int centerY = recyclerView.getHeight() / 2;
                int closestPosition = -1;
                float minDistance = Float.MAX_VALUE;

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    int childCenterY = (child.getTop() + child.getBottom()) / 2;
                    float distanceFromCenter = Math.abs(centerY - childCenterY);

                    if (distanceFromCenter < minDistance) {
                        minDistance = distanceFromCenter;
                        closestPosition = recyclerView.getChildAdapterPosition(child);
                    }

                    float maxDistance = recyclerView.getHeight() / 2f;
                    float alpha = 1f - (distanceFromCenter / maxDistance);
                    alpha = Math.max(0.3f, alpha);
                    child.setAlpha(alpha);

                    float scale = 1f - (distanceFromCenter / maxDistance) * 0.5f;
                    scale = Math.max(0.5f, scale);
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                }

                if (closestPosition != centerPosition) {
                    centerPosition = closestPosition;
                    adapter.setHighlightedPosition(centerPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter = new MenuAdapter(this, menuItems, position -> {
            if (position == centerPosition) {
                switch (position) {
                    case 0: // Exercise
                        startActivity(new Intent(MenuActivity.this, ExerciseListActivity.class));
                        break;
                    case 1: // Caffeine
                        startActivity(new Intent(MenuActivity.this, CaffeineMenuActivity.class));
                        break;
                    case 2: // Breathing
                        startActivity(new Intent(MenuActivity.this, BreathingMenuActivity.class));
                        break;
                    case 3: // Settings
                        startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.scrollToPosition(menuItems.size() / 2);
    }

    private static class CustomLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        private static final float MAX_CHILD_SCALE = 1.0f;
        private static final float MIN_CHILD_SCALE = 0.65f;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {
            float centerOffset = ((child.getHeight() / 2f) - parent.getHeight() / 2f) / parent.getHeight();
            float yRelativeToCenter = (child.getY() - parent.getHeight() / 2f) / parent.getHeight();

            float scale = MAX_CHILD_SCALE - (MAX_CHILD_SCALE - MIN_CHILD_SCALE) * Math.abs(yRelativeToCenter);
            child.setScaleX(scale);
            child.setScaleY(scale);

            float xOffset = (float) Math.sin(centerOffset * Math.PI) * 20f;
            child.setTranslationX(xOffset);
        }
    }
}