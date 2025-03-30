package com.example.wellnesswrist;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import com.example.wellnesswrist.databinding.ActivityExerciseListBinding;
import java.util.Arrays;
import java.util.List;

public class ExerciseListActivity extends ComponentActivity {
    private ActivityExerciseListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WearableRecyclerView recyclerView = binding.exerciseRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEdgeItemsCenteringEnabled(true);

        List<ExerciseItem> exercises = Arrays.asList(
                new ExerciseItem("Resting", R.drawable.ic_resting),
                new ExerciseItem("After Exercise", R.drawable.ic_after_exercise)
        );

        ExerciseAdapter adapter = new ExerciseAdapter(exercises, exercise -> {
            Intent intent = new Intent(this, ExerciseInputActivity.class);
            intent.putExtra("exercise_type", exercise);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}

class ExerciseItem {
    String name;
    int iconResId;

    ExerciseItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }
}

class ExerciseAdapter extends WearableRecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private final List<ExerciseItem> exercises;
    private final ExerciseClickListener listener;

    interface ExerciseClickListener {
        void onExerciseClick(String exercise);
    }

    ExerciseAdapter(List<ExerciseItem> exercises, ExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
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
        holder.textView.setText(item.name);
        holder.imageView.setImageResource(item.iconResId);
        holder.itemView.setOnClickListener(v -> listener.onExerciseClick(item.name));
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