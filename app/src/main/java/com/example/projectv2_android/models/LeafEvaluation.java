package com.example.projectv2_android.models;

import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;

public class LeafEvaluation extends Evaluation {
    private final NoteRepository noteRepository;

    public LeafEvaluation(String name, long classId, Long parentId, Integer pointsMax, NoteRepository noteRepository) {
        super(name, classId, parentId, pointsMax, true); // isLeaf = true
        this.noteRepository = noteRepository;
    }

    public NoteRepository getNoteRepository() {
        return noteRepository;
    }

    @Override
    public double calculateScoreForStudent(long studentId) {
        Note note = noteRepository.getNoteForStudent(this.getId(), studentId);
        return (note != null && note.getNoteValue() != null) ? note.getNoteValue() : 0.0;
    }

    public double calculateOverallScore() {
        List<Note> notes = noteRepository.getNotesForEvaluation(getId());
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        for (Note note : notes) {
            totalScore += note.getNoteValue() != null ? note.getNoteValue() : 0.0;
        }

        return totalScore / notes.size();
    }
}
