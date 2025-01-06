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
import com.example.projectv2_android.services.EvaluationService;

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
    private final EvaluationService evaluationService; // Service des évaluations
    private OnNoteClickListener noteClickListener;
    private long studentId;

    public interface OnForceAverageClickListener {
        void onForceAverageClicked(long evaluationId, double maxPoints);
    }

    public interface OnNoteClickListener {
        void onNoteClick(long evaluationId);
    }

    public StudentEvaluationsAdapter(long studentId, OnNoteClickListener noteClickListener, OnForceAverageClickListener forceAverageListener, EvaluationService evaluationService) {
        this.studentId = studentId;
        this.noteClickListener = noteClickListener;
        this.forceAverageListener = forceAverageListener;
        this.evaluationService = evaluationService; // Passer une instance existante du service
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
        return new ViewHolder(view);
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
                notifyItemChanged(position); // Mettre à jour l'icône après modification
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
                forceAverageListener.onForceAverageClicked(evaluation.getId(), evaluation.getPointsMax());
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

            if (note != null && note.getForcedValue() != null) {
                // Afficher la note forcée
                textEvaluationNote.setText(String.format(
                        Locale.getDefault(),
                        "%.2f / %d (forcée)",
                        note.getForcedValue(),
                        evaluation.getPointsMax()
                ));
            } else if (note != null && note.getNoteValue() != null) {
                // Afficher la note normale
                textEvaluationNote.setText(String.format(
                        Locale.getDefault(),
                        "%.2f / %d",
                        note.getNoteValue(),
                        evaluation.getPointsMax()
                ));
            } else if (!evaluation.isLeaf()) {
                textEvaluationNote.setText("Calcul en cours...");
                evaluationService.calculateWeightedAverageAsync(studentId, evaluation.getId(), new EvaluationService.Callback<Double>() {
                    @Override
                    public void onResult(Double result) {
                        textEvaluationNote.post(() -> {
                            // Arrondi à 0,5 près
                            double roundedResult = Math.round(result * 2) / 2.0;
                            String noteForcedText = (note != null && note.getForcedValue() != null) ? " (forcée)" : "";
                            textEvaluationNote.setText(String.format(
                                    Locale.getDefault(),
                                    "Moyenne : %.1f / %d%s",
                                    roundedResult,
                                    evaluation.getPointsMax(),
                                    noteForcedText
                            ));
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        textEvaluationNote.post(() -> textEvaluationNote.setText("Erreur de calcul"));
                    }
                });
            } else {
                textEvaluationNote.setText("Non noté");
            }

            if (evaluation.isLeaf()) {
                // Icône d'expansion non visible pour les feuilles
                iconExpand.setVisibility(View.GONE);
                buttonForceAverage.setVisibility(View.GONE); // Cacher le bouton pour les enfants
            } else {
                // Icône visible pour les évaluations parentes
                iconExpand.setVisibility(View.VISIBLE);
                iconExpand.setImageResource(
                        expandedEvaluationIds.contains(evaluation.getId())
                                ? R.drawable.ic_expand_less
                                : R.drawable.ic_expand_more
                );
                buttonForceAverage.setVisibility(View.VISIBLE); // Afficher le bouton pour les parents
            }

            // Désactiver le clic sur les parents directement
            itemView.setOnClickListener(v -> {
                if (evaluation.isLeaf() && noteClickListener != null) {
                    noteClickListener.onNoteClick(evaluation.getId());
                }
            });

            // Gestion de l'état forcé
            if (note != null && note.getForcedValue() != null) {
                buttonForceAverage.setText("Modifier la note forcée");
                buttonForceAverage.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.forced_note_color));
            } else {
                buttonForceAverage.setText("Forcer la moyenne");
                buttonForceAverage.setBackgroundTintList(itemView.getContext().getResources().getColorStateList(R.color.default_button_color));
            }
        }


    }


}
