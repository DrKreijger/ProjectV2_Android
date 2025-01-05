package com.example.projectv2_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;

import java.util.ArrayList;
import java.util.List;

public class StudentEvaluationsAdapter extends RecyclerView.Adapter<StudentEvaluationsAdapter.ViewHolder> {

    private final List<Evaluation> evaluations = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();
    private final List<Long> expandedEvaluations = new ArrayList<>(); // Track expanded evaluations
    private final OnForceAverageClickListener forceAverageListener;
    private OnNoteClickListener noteClickListener;

    public interface OnForceAverageClickListener {
        void onForceAverageClicked(long evaluationId);
    }

    public StudentEvaluationsAdapter(OnNoteClickListener noteClickListener, OnForceAverageClickListener forceAverageListener) {
        this.noteClickListener = noteClickListener;
        this.forceAverageListener = forceAverageListener;
    }

    public interface OnNoteClickListener {
        void onNoteClick(long evaluationId);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.noteClickListener = listener;
    }

    public void setData(List<Evaluation> evaluations, List<Note> notes) {
        this.evaluations.clear();
        this.notes.clear();

        if (evaluations != null) {
            this.evaluations.addAll(evaluations);
        }

        if (notes != null) {
            this.notes.addAll(notes);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_evaluation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 0 && position < evaluations.size()) {
            Evaluation evaluation = evaluations.get(position);

            // Bind evaluation data
            holder.bind(evaluation);

            // Expand/collapse sub-evaluations
            holder.itemView.setOnClickListener(v -> {
                if (!evaluation.isLeaf()) {
                    if (expandedEvaluations.contains(evaluation.getId())) {
                        expandedEvaluations.remove(evaluation.getId());
                    } else {
                        expandedEvaluations.add(evaluation.getId());
                    }
                    notifyItemChanged(position);
                }
            });

            // Force average button
            holder.buttonForceAverage.setOnClickListener(v -> {
                if (forceAverageListener != null) {
                    forceAverageListener.onForceAverageClicked(evaluation.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return evaluations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEvaluationName, textEvaluationNote;
        Button buttonForceAverage;
        ImageView iconExpand; // Expand/collapse icon

        ViewHolder(View itemView) {
            super(itemView);
            textEvaluationName = itemView.findViewById(R.id.text_evaluation_name);
            textEvaluationNote = itemView.findViewById(R.id.text_evaluation_note);
            buttonForceAverage = itemView.findViewById(R.id.button_force_average);
            iconExpand = itemView.findViewById(R.id.icon_expand); // Reference the ImageView
        }

        public void bind(Evaluation evaluation) {
            textEvaluationName.setText(evaluation.getName());

            if (evaluation.isLeaf()) {
                iconExpand.setVisibility(View.GONE);
            } else {
                iconExpand.setVisibility(View.VISIBLE);
            }
        }
    }
}
