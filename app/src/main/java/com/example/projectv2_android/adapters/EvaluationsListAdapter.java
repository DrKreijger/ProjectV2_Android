package com.example.projectv2_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.models.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationsListAdapter extends RecyclerView.Adapter<EvaluationsListAdapter.EvaluationViewHolder> {

    private final List<Evaluation> evaluations = new ArrayList<>();
    private final Map<Long, Boolean> expandedEvaluations = new HashMap<>(); // To keep track of expanded evaluations
//    private final OnEvaluationClickListener listener;

    public interface OnEvaluationClickListener {
        void onEvaluationClick(Evaluation evaluation);
    }

//    public EvaluationsListAdapter(OnEvaluationClickListener listener) {
//        this.listener = listener;
//    }

    public void setData(List<Evaluation> evaluations) {
        this.evaluations.clear();
        this.evaluations.addAll(evaluations);

        // Reset expansion states for new data
        for (Evaluation evaluation : evaluations) {
            if (!evaluation.isLeaf()) {
                expandedEvaluations.put(evaluation.getId(), false); // Default to collapsed
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EvaluationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evaluation, parent, false);
        return new EvaluationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationViewHolder holder, int position) {
        Evaluation evaluation = evaluations.get(position);

        // Determine if the item should be visible based on parent expansion
        boolean isVisible = isItemVisible(position);
        if (!isVisible) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0)); // Collapse the item completely
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            holder.itemView.setLayoutParams(params); // Restore proper layout
            holder.bind(evaluation);
        }
    }

    @Override
    public int getItemCount() {
        return evaluations.size();
    }

    /**
     * Determine if the item at the given position should be visible.
     */
    private boolean isItemVisible(int position) {
        Evaluation evaluation = evaluations.get(position);

        // Always show parent evaluations
        if (evaluation.getParentId() == null) {
            return true;
        }

        // Show child evaluations only if their parent is expanded
        return expandedEvaluations.getOrDefault(evaluation.getParentId(), false);
    }

    class EvaluationViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textPointsMax;
        private final TextView textType;
        private final ImageView iconExpand; // Icon for expand/collapse

        public EvaluationViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_evaluation_name);
            textPointsMax = itemView.findViewById(R.id.text_points_max);
            textType = itemView.findViewById(R.id.text_evaluation_type);
            iconExpand = itemView.findViewById(R.id.icon_expand); // Reference to the expand icon

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Evaluation evaluation = evaluations.get(position);

                    // Toggle expansion for parent evaluations
                    if (!evaluation.isLeaf()) {
                        boolean isExpanded = expandedEvaluations.getOrDefault(evaluation.getId(), false);
                        expandedEvaluations.put(evaluation.getId(), !isExpanded);
                        notifyDataSetChanged(); // Refresh list to show/hide children
                    }

                    // Notify listener of click
//                    listener.onEvaluationClick(evaluation);
                }
            });
        }

        public void bind(Evaluation evaluation) {
            textName.setText(evaluation.getName());
            textPointsMax.setText(String.format("%d pts", evaluation.getPointsMax()));

            if (evaluation.isLeaf()) {
                textType.setText(R.string.leaf_evaluation); // Text for leaf
                iconExpand.setVisibility(View.GONE); // No expand icon for leaf
            } else {
                textType.setText(R.string.parent_evaluation); // Text for parent
                iconExpand.setVisibility(View.VISIBLE); // Show expand icon for parent

                // Set icon rotation based on expansion state
                boolean isExpanded = expandedEvaluations.getOrDefault(evaluation.getId(), false);
                iconExpand.setRotation(isExpanded ? 180f : 0f);
            }
        }
    }
}
