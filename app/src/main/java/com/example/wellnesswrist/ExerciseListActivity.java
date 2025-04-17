package com.example.wellnesswrist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.wellnesswrist.databinding.ActivityExerciseListBinding;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class ExerciseListActivity extends ComponentActivity {
    private ActivityExerciseListBinding binding;
    private ExerciseAdapter adapter;
    private List<ExerciseItem> exercises;
    private WearableLinearLayoutManager layoutManager;
    private int highlightedPosition = 2; // Default to "Walking" (middle item, position 2)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WearableRecyclerView recyclerView = binding.exerciseRecyclerView;
        // for linear layout
        layoutManager = new WearableLinearLayoutManager(this, new WearableLinearLayoutManager.LayoutCallback() {
            @Override
            public void onLayoutFinished(View child, RecyclerView parent) {
                // ✅ Flatten visual scaling
                child.setScaleX(1f);
                child.setScaleY(1f);
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEdgeItemsCenteringEnabled(true);

// Optional: also disable gesture-based circular scroll
        recyclerView.setCircularScrollingGestureEnabled(false);
        recyclerView.setScrollDegreesPerScreen(0);


        // Snap to center
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        exercises = Arrays.asList(
                new ExerciseItem("Running", R.drawable.ic_running),
                new ExerciseItem("Cycling", R.drawable.ic_cycling),
                new ExerciseItem("Walking", R.drawable.ic_walking),
                new ExerciseItem("Swimming", R.drawable.ic_swimming),
                new ExerciseItem("Badminton", R.drawable.ic_badminton)
        );

        adapter = new ExerciseAdapter(this, exercises, position -> {
            Intent intent = new Intent(this, ExerciseInputActivity.class);
            intent.putExtra("exercise_type", exercises.get(position).getName());
            startActivity(intent);
        });

        adapter.setHighlightedPosition(highlightedPosition);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(highlightedPosition);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int centerPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (centerPosition != RecyclerView.NO_POSITION && centerPosition != highlightedPosition) {
                    highlightedPosition = centerPosition;
                    adapter.setHighlightedPosition(highlightedPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}

class ExerciseItem {
    String name;
    int iconResId;

    ExerciseItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}

class ExerciseAdapter extends WearableRecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private final List<ExerciseItem> exercises;
    private final Context context;
    private final OnItemClickListener listener;
    private int highlightedPosition = -1;

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    ExerciseAdapter(Context context, List<ExerciseItem> exercises, OnItemClickListener listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    public void setHighlightedPosition(int position) {
        this.highlightedPosition = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExerciseItem item = exercises.get(position);
        holder.textView.setText(item.getName());
        holder.imageView.setImageResource(item.getIconResId());

        // Highlight the center item
        holder.itemView.setSelected(position == highlightedPosition);

        // Increase text size for the selected item
        if (position == highlightedPosition) {
            holder.textView.setTextSize(16); // Larger text size for selected item
        } else {
            holder.textView.setTextSize(14); // Default text size for unselected items
        }

        // Restrict clicks to the center item
        holder.itemView.setOnClickListener(v -> {
            if (position == highlightedPosition) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends WearableRecyclerView.ViewHolder {
        android.widget.TextView textView;
        android.widget.ImageView imageView;

        ViewHolder(android.view.View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.exercise_text);
            imageView = itemView.findViewById(R.id.exercise_icon);
        }
    }
}