package com.example.projectv2_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        private final TextView textType; // For identifying Parent/Leaf evaluation

        public EvaluationViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_evaluation_name);
            textPointsMax = itemView.findViewById(R.id.text_points_max);
            textType = itemView.findViewById(R.id.text_evaluation_type);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEvaluationClick(evaluations.get(position));
                }
            });
        }

        public void bind(Evaluation evaluation) {
            textName.setText(evaluation.getName());
            textPointsMax.setText(String.format("%d pts", evaluation.getPointsMax()));

            if (evaluation.isLeaf()) {
                textType.setText(R.string.leaf_evaluation); // Text for leaf
            } else {
                textType.setText(R.string.parent_evaluation); // Text for parent
            }
        }
    }
}
