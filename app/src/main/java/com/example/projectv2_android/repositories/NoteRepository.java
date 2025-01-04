package com.example.projectv2_android.repositories;

import com.example.projectv2_android.dao.NoteDao;
import com.example.projectv2_android.models.Note;

import java.util.List;

public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    public long insertNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        return noteDao.insertNote(note);
    }

    public int updateNote(Note note) {
        if (note == null || note.getId() == 0) {
            throw new IllegalArgumentException("Invalid note for update");
        }
        return noteDao.updateNote(note);
    }

    public Note getNoteForStudent(long evalId, long studentId) {
        if (evalId <= 0 || studentId <= 0) {
            throw new IllegalArgumentException("Invalid evaluation or student ID");
        }
        return noteDao.getNoteForStudent(evalId, studentId);
    }

    public List<Note> getNotesForEvaluation(long evalId) {
        if (evalId <= 0) {
            throw new IllegalArgumentException("Invalid evaluation ID");
        }
        return noteDao.getNotesForEvaluation(evalId);
    }

    public int deleteNotesForEvaluation(long evalId) {
        if (evalId <= 0) {
            throw new IllegalArgumentException("Invalid evaluation ID for deletion");
        }
        return noteDao.deleteNotesForEvaluation(evalId);
    }

    public int deleteNotesForStudent(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID for deletion");
        }
        return noteDao.deleteNotesForStudent(studentId);
    }

    public List<Note> getNotesForStudent(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }
        return noteDao.getNotesForStudent(studentId);
    }

}
