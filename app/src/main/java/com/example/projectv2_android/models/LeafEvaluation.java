package com.example.projectv2_android.models;

import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;

public class LeafEvaluation extends Evaluation {
    private final NoteRepository noteRepository;

    public LeafEvaluation(String name, long classId, Long parentId, Integer pointsMax, NoteRepository noteRepository) {
        super(name, classId, parentId, pointsMax);
        this.noteRepository = noteRepository;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public NoteRepository getNoteRepository() {
        return noteRepository;
    }

    @Override
    public double calculateScoreForStudent(long studentId) {
        // Obtenez la note pour cet étudiant pour cette évaluation
        Note note = noteRepository.getNoteForStudent(this.getId(), studentId);
        if (note != null) {
            return note.getNoteValue();
        } else {
            // Retourne 0 si aucune note n'existe
            return 0.0;
        }
    }

    /**
     * Calcul de la moyenne générale des notes pour cette évaluation.
     * @return La moyenne générale des notes pour l'évaluation.
     */
    public double calculateOverallScore() {
        List<Note> notes = noteRepository.getNotesForEvaluation(getId());
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        for (Note note : notes) {
            totalScore += note.getNoteValue();
        }

        return totalScore / notes.size();
    }
}
