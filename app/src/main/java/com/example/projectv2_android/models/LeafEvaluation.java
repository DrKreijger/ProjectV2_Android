package com.example.projectv2_android.models;

import com.example.projectv2_android.repositories.NoteRepository;

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
}
