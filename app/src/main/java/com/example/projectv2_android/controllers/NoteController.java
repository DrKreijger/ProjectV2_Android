package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;

public class NoteController {
    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getNotesForEvaluation(long evalId) {
        return noteRepository.getNotesForEvaluation(evalId);
    }

    public Note getNoteForStudent(long evalId, long studentId) {
        return noteRepository.getNoteForStudent(evalId, studentId);
    }

    public List<Note> getNotesForStudent(long studentId) {
        return noteRepository.getNotesForStudent(studentId);
    }

    public long addNote(long evalId, long studentId, double noteValue) {
        Note note = new Note();
        note.setEvalId(evalId);
        note.setStudentId(studentId);
        note.setNoteValue(noteValue);
        return noteRepository.insertNote(note);
    }

    public boolean updateNote(long evalId, long studentId, double newNoteValue) {
        Note existingNote = noteRepository.getNoteForStudent(evalId, studentId);
        if (existingNote != null) {
            existingNote.setNoteValue(newNoteValue);
            return noteRepository.updateNote(existingNote) > 0;
        }
        return false;
    }

    public void forceNoteForEvaluation(long studentId, long evaluationId, double forcedNote) {
        noteRepository.updateForcedNoteForStudentEvaluation(studentId, evaluationId, forcedNote);
    }


    public boolean deleteNotesForEvaluation(long evalId) {
        return noteRepository.deleteNotesForEvaluation(evalId) > 0;
    }
}
