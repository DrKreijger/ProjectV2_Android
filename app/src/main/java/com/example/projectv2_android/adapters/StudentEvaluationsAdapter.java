package com.example.projectv2_android.adapters;

import android.util.Log;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentEvaluationsAdapter extends RecyclerView.Adapter<StudentEvaluationsAdapter.ViewHolder> {

    private final List<Evaluation> evaluations = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();
    private final Set<Long> expandedEvaluations = new HashSet<>(); // Track expanded evaluations
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
            for (Evaluation evaluation : evaluations) {
                Log.d("StudentEvaluationsAdapter", "Évaluation ajoutée : " + evaluation.getName() + " | ID : " + evaluation.getId());
            }
            this.evaluations.addAll(evaluations);
        }

        if (notes != null) {
            for (Note note : notes) {
                Log.d("StudentEvaluationsAdapter", "Note ajoutée : EvaluationID=" + note.getEvalId() + ", NoteValue=" + note.getNoteValue());
            }
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

            // Récupérer la note correspondante pour cette évaluation
            Note correspondingNote = null;
            for (Note note : notes) {
                if (note.getEvalId() == evaluation.getId()) {
                    correspondingNote = note;
                    break;
                }
            }

            // Bind evaluation data avec la note correspondante
            holder.bind(evaluation, correspondingNote, expandedEvaluations.contains(evaluation.getId()));

            // Expand/collapse sub-evaluations
            holder.iconExpand.setOnClickListener(v -> {
                if (!evaluation.isLeaf()) {
                    if (expandedEvaluations.contains(evaluation.getId())) {
                        expandedEvaluations.remove(evaluation.getId());
                    } else {
                        expandedEvaluations.add(evaluation.getId());
                    }
                    notifyItemChanged(position);
                }
            });

            // Listener for note click
            holder.itemView.setOnClickListener(v -> {
                if (noteClickListener != null) {
                    Log.d("StudentEvaluationsAdapter", "Clique sur l'évaluation : " + evaluation.getName() + " | ID : " + evaluation.getId());
                    noteClickListener.onNoteClick(evaluation.getId());
                } else {
                    Log.w("StudentEvaluationsAdapter", "NoteClickListener n'est pas défini");
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

        public void bind(Evaluation evaluation, Note note, boolean isExpanded) {
            textEvaluationName.setText(evaluation.getName());

            // Log pour le débogage
            Log.d("StudentEvaluationsAdapter", "Binding evaluation: " + evaluation.getName() + " | ID: " + evaluation.getId());
            if (note != null) {
                Log.d("StudentEvaluationsAdapter", "Note associée: " + note.getNoteValue());
            } else {
                Log.d("StudentEvaluationsAdapter", "Aucune note associée trouvée.");
            }

            // Afficher la note ou un texte par défaut
            if (note != null && note.getNoteValue() != null) {
                textEvaluationNote.setText(String.format("%.2f / %d", note.getNoteValue(), evaluation.getPointsMax()));
            } else {
                textEvaluationNote.setText(evaluation.isLeaf() ? "Non noté" : "Évaluation parente");
            }

            // Gérer l'icône d'expand/collapse
            if (evaluation.isLeaf()) {
                iconExpand.setVisibility(View.GONE);
            } else {
                iconExpand.setVisibility(View.VISIBLE);
                iconExpand.setImageResource(isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
            }
        }
    }

}
