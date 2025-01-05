package com.example.projectv2_android.adapters;

import android.util.Log;
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

    private final List<Evaluation> evaluations = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();
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
            Log.d("StudentEvaluationsAdapter", "Nombre d'évaluations : " + evaluations.size());
        }

        if (notes != null) {
            this.notes.addAll(notes);
            Log.d("StudentEvaluationsAdapter", "Nombre de notes : " + notes.size());
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

            // Récupération sécurisée de la note
            Note note = null;
            if (notes != null && position < notes.size()) {
                note = notes.get(position);
            }

            // Nom de l'évaluation
            holder.textEvaluationName.setText(evaluation.getName());

            // Affichage de la note ou de l'état "Non noté"
            if (note != null && note.getNoteValue() != null) {
                holder.textEvaluationNote.setText(String.format("%.1f / %d", note.getNoteValue(), evaluation.getPointsMax()));
            } else {
                holder.textEvaluationNote.setText("Non noté");
            }

            // Listener pour cliquer sur une évaluation et ouvrir le dialog de note
            holder.itemView.setOnClickListener(v -> {
                if (noteClickListener != null) {
                    noteClickListener.onNoteClick(evaluation.getId());
                } else {
                    Log.w("StudentEvaluationsAdapter", "NoteClickListener n'est pas défini");
                }
            });

            // Listener pour forcer une moyenne
            holder.buttonForceAverage.setOnClickListener(v -> {
                if (forceAverageListener != null) {
                    forceAverageListener.onForceAverageClicked(evaluation.getId());
                } else {
                    Log.w("StudentEvaluationsAdapter", "ForceAverageListener n'est pas défini");
                }
            });
        } else {
            Log.w("StudentEvaluationsAdapter", "Position invalide : " + position);
        }
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
