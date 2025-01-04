package com.example.projectv2_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;

import java.util.ArrayList;
import java.util.List;

public class StudentEvaluationsAdapter extends RecyclerView.Adapter<StudentEvaluationsAdapter.ViewHolder> {

    private List<Evaluation> evaluations = new ArrayList<>();
    private List<Note> notes = new ArrayList<>();
    private final OnForceAverageClickListener forceAverageListener;

    public interface OnForceAverageClickListener {
        void onForceAverageClicked(long evaluationId);
    }

    public StudentEvaluationsAdapter(OnForceAverageClickListener listener) {
        this.forceAverageListener = listener;
    }

    public void setData(List<Evaluation> evaluations, List<Note> notes) {
        this.evaluations = evaluations;
        this.notes = notes;
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
        Evaluation evaluation = evaluations.get(position);
        Note note = notes.get(position);

        holder.textEvaluationName.setText(evaluation.getName());
        holder.textEvaluationNote.setText(String.format("%.1f / %d", note.getNoteValue(), evaluation.getPointsMax()));

        holder.buttonForceAverage.setOnClickListener(v ->
                forceAverageListener.onForceAverageClicked(evaluation.getId()));
    }

    @Override
    public int getItemCount() {
        return evaluations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEvaluationName, textEvaluationNote;
        Button buttonForceAverage;

        ViewHolder(View itemView) {
            super(itemView);
            textEvaluationName = itemView.findViewById(R.id.text_evaluation_name);
            textEvaluationNote = itemView.findViewById(R.id.text_evaluation_note);
            buttonForceAverage = itemView.findViewById(R.id.button_force_average);
        }
    }
}
