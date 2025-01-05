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
import java.util.List;

public class EvaluationsListAdapter extends RecyclerView.Adapter<EvaluationsListAdapter.EvaluationViewHolder> {

    private final List<Evaluation> evaluations = new ArrayList<>();
    private final OnEvaluationClickListener listener;

    private final List<Long> expandedEvaluations = new ArrayList<>(); // To keep track of expanded evaluations

    public interface OnEvaluationClickListener {
        void onEvaluationClick(Evaluation evaluation);
    }

    public EvaluationsListAdapter(OnEvaluationClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Evaluation> evaluations) {
        this.evaluations.clear();
        this.evaluations.addAll(evaluations);
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
        holder.bind(evaluation);
    }

    @Override
    public int getItemCount() {
        return evaluations.size();
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

                    // Toggle expansion
                    if (!evaluation.isLeaf()) {
                        if (expandedEvaluations.contains(evaluation.getId())) {
                            expandedEvaluations.remove(evaluation.getId());
                        } else {
                            expandedEvaluations.add(evaluation.getId());
                        }
                        notifyItemChanged(position);
                    }

                    listener.onEvaluationClick(evaluation);
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
                iconExpand.setRotation(expandedEvaluations.contains(evaluation.getId()) ? 180f : 0f);
            }
        }
    }
}
