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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StudentEvaluationsAdapter extends RecyclerView.Adapter<StudentEvaluationsAdapter.ViewHolder> {

    private final List<Evaluation> originalEvaluations = new ArrayList<>(); // Liste complète des évaluations
    private final List<Evaluation> displayedEvaluations = new ArrayList<>(); // Liste affichée
    private final List<Note> notes = new ArrayList<>();
    private final Set<Long> expandedEvaluationIds = new HashSet<>(); // IDs des évaluations expandées
    private final OnForceAverageClickListener forceAverageListener;
    private OnNoteClickListener noteClickListener;

    public interface OnForceAverageClickListener {
        void onForceAverageClicked(long evaluationId);
    }

    public interface OnNoteClickListener {
        void onNoteClick(long evaluationId);
    }

    public StudentEvaluationsAdapter(OnNoteClickListener noteClickListener, OnForceAverageClickListener forceAverageListener) {
        this.noteClickListener = noteClickListener;
        this.forceAverageListener = forceAverageListener;
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.noteClickListener = listener;
    }

    public void setData(List<Evaluation> evaluations, List<Note> notes) {
        originalEvaluations.clear();
        displayedEvaluations.clear();
        this.notes.clear();

        if (evaluations != null) {
            originalEvaluations.addAll(evaluations);
            // Ajouter seulement les évaluations parents par défaut à la liste affichée
            for (Evaluation evaluation : originalEvaluations) {
                if (evaluation.getParentId() == null) { // Les évaluations sans parent
                    displayedEvaluations.add(evaluation);
                }
            }
        }

        if (notes != null) {
            this.notes.addAll(notes);
        }

        expandedEvaluationIds.clear(); // Réinitialiser l'état des expansions
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_evaluation, parent, false);
        return new ViewHolder(view); // Pas besoin de passer l'adaptateur ici
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Evaluation evaluation = displayedEvaluations.get(position);

        // Trouver la note correspondante
        Note correspondingNote = null;
        for (Note note : notes) {
            if (note.getEvalId() == evaluation.getId()) {
                correspondingNote = note;
                break;
            }
        }

        // Lier les données de l'évaluation et de la note
        holder.bind(evaluation, correspondingNote);

        // Gestion de l'icône expand/collapse
        holder.iconExpand.setOnClickListener(v -> {
            if (!evaluation.isLeaf()) {
                if (expandedEvaluationIds.contains(evaluation.getId())) {
                    collapse(evaluation);
                } else {
                    expand(evaluation);
                }
                // Rafraîchir le ViewHolder après modification
                notifyItemChanged(position);
            }
        });

        // Gestion du clic pour modifier/ajouter une note
        holder.itemView.setOnClickListener(v -> {
            if (noteClickListener != null) {
                noteClickListener.onNoteClick(evaluation.getId());
            }
        });

        // Gestion du clic sur "Forcer la moyenne"
        holder.buttonForceAverage.setOnClickListener(v -> {
            if (forceAverageListener != null) {
                forceAverageListener.onForceAverageClicked(evaluation.getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return displayedEvaluations.size();
    }

    private void expand(Evaluation evaluation) {
        int position = displayedEvaluations.indexOf(evaluation);
        if (position != -1) {
            List<Evaluation> subEvaluations = getSubEvaluations(evaluation.getId());
            displayedEvaluations.addAll(position + 1, subEvaluations);
            expandedEvaluationIds.add(evaluation.getId());
            notifyItemRangeInserted(position + 1, subEvaluations.size());
        }
    }

    private void collapse(Evaluation evaluation) {
        int position = displayedEvaluations.indexOf(evaluation);
        if (position != -1) {
            List<Evaluation> subEvaluations = getSubEvaluations(evaluation.getId());
            displayedEvaluations.removeAll(subEvaluations);
            expandedEvaluationIds.remove(evaluation.getId());
            notifyItemRangeRemoved(position + 1, subEvaluations.size());
        }
    }

    private List<Evaluation> getSubEvaluations(long parentId) {
        List<Evaluation> subEvaluations = new ArrayList<>();
        for (Evaluation evaluation : originalEvaluations) {
            if (evaluation.getParentId() != null && evaluation.getParentId() == parentId) {
                subEvaluations.add(evaluation);
            }
        }
        return subEvaluations;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEvaluationName, textEvaluationNote;
        Button buttonForceAverage;
        ImageView iconExpand;

        public ViewHolder(View itemView) {
            super(itemView);
            textEvaluationName = itemView.findViewById(R.id.text_evaluation_name);
            textEvaluationNote = itemView.findViewById(R.id.text_evaluation_note);
            buttonForceAverage = itemView.findViewById(R.id.button_force_average);
            iconExpand = itemView.findViewById(R.id.icon_expand);
        }

        public void bind(Evaluation evaluation, Note note) {
            textEvaluationName.setText(evaluation.getName());

            if (note != null && note.getNoteValue() != null) {
                textEvaluationNote.setText(String.format(Locale.getDefault(), "%.2f / %d", note.getNoteValue(), evaluation.getPointsMax()));
            } else {
                textEvaluationNote.setText(evaluation.isLeaf() ? "Non noté" : "Évaluation parente");
            }

            if (evaluation.isLeaf()) {
                iconExpand.setVisibility(View.GONE);
            } else {
                iconExpand.setVisibility(View.VISIBLE);
                // Mettre à jour l'icône en fonction de l'état actuel
                iconExpand.setImageResource(expandedEvaluationIds.contains(evaluation.getId()) ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
            }
        }

    }
}
