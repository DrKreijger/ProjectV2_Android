package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;

public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Ajoute ou met à jour une note pour un étudiant dans une évaluation.
     */
    public void setNoteForEvaluationAndStudent(long evalId, long studentId, double noteValue) {
        if (noteValue < 0) {
            throw new IllegalArgumentException("Note value cannot be negative");
        }

        Note existingNote = noteRepository.getNoteForStudent(evalId, studentId);
        if (existingNote != null) {
            // Si la note existe, mettez-la à jour
            existingNote.setNoteValue(noteValue);
            noteRepository.updateNote(existingNote);
        } else {
            // Sinon, insérez une nouvelle note
            Note newNote = new Note();
            newNote.setEvalId(evalId);
            newNote.setStudentId(studentId);
            newNote.setNoteValue(noteValue);
            noteRepository.insertNote(newNote);
        }
    }

    /**
     * Force une note spécifique pour un étudiant dans une évaluation.
     */
    public void setForcedNoteForEvaluationAndStudent(long evalId, long studentId, double forcedValue) {
        if (forcedValue < 0) {
            throw new IllegalArgumentException("Forced note value cannot be negative");
        }

        Note existingNote = noteRepository.getNoteForStudent(evalId, studentId);
        if (existingNote != null) {
            // Si la note existe, forcez sa valeur
            existingNote.setForcedValue(forcedValue);
            noteRepository.updateNote(existingNote);
        } else {
            // Sinon, créez une nouvelle note avec une valeur forcée
            Note newNote = new Note();
            newNote.setEvalId(evalId);
            newNote.setStudentId(studentId);
            newNote.setForcedValue(forcedValue);
            noteRepository.insertNote(newNote);
        }
    }

    /**
     * Récupère toutes les notes pour une évaluation donnée.
     */
    public List<Note> getNotesForEvaluation(long evalId) {
        return noteRepository.getNotesForEvaluation(evalId);
    }

    /**
     * Supprime toutes les notes pour une évaluation donnée.
     */
    public int deleteNotesForEvaluation(long evalId) {
        return noteRepository.deleteNotesForEvaluation(evalId);
    }

    /**
     * Supprime toutes les notes pour un étudiant donné.
     */
    public int deleteNotesForStudent(long studentId) {
        return noteRepository.deleteNotesForStudent(studentId);
    }
}
