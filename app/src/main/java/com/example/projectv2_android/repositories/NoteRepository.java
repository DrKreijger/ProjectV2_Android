package com.example.projectv2_android.repositories;

import com.example.projectv2_android.dao.NoteDao;
import com.example.projectv2_android.models.Note;

import java.util.List;

public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    /**
     * Insère une nouvelle note dans la base de données.
     */
    public long insertNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("La note ne peut pas être nulle");
        }
        return noteDao.insertNote(note);
    }

    /**
     * Met à jour une note existante.
     */
    public int updateNote(Note note) {
        if (note == null || note.getId() == 0) {
            throw new IllegalArgumentException("La note est invalide pour la mise à jour");
        }
        return noteDao.updateNote(note);
    }

    /**
     * Insère ou met à jour une note dans la base de données.
     */
    public long insertOrUpdate(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("La note ne peut pas être nulle");
        }

        try {
            Note existingNote = noteDao.getNoteForStudent(note.getEvalId(), note.getStudentId());
            if (existingNote != null) {
                // Mise à jour de la note existante
                note.setId(existingNote.getId());
                noteDao.updateNote(note);
                return note.getId();
            } else {
                // Insertion d'une nouvelle note
                return noteDao.insertNote(note);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'insertion ou de la mise à jour de la note", e);
        }
    }

    /**
     * Récupère une note pour un étudiant et une évaluation spécifique.
     */
    public Note getNoteForStudent(long evalId, long studentId) {
        if (evalId <= 0 || studentId <= 0) {
            throw new IllegalArgumentException("L'ID de l'évaluation ou de l'étudiant est invalide");
        }
        return noteDao.getNoteForStudent(evalId, studentId);
    }

    /**
     * Récupère toutes les notes pour une évaluation spécifique.
     */
    public List<Note> getNotesForEvaluation(long evalId) {
        if (evalId <= 0) {
            throw new IllegalArgumentException("L'ID de l'évaluation est invalide");
        }
        return noteDao.getNotesForEvaluation(evalId);
    }

    /**
     * Supprime toutes les notes liées à une évaluation.
     */
    public int deleteNotesForEvaluation(long evalId) {
        if (evalId <= 0) {
            throw new IllegalArgumentException("L'ID de l'évaluation est invalide pour la suppression");
        }
        return noteDao.deleteNotesForEvaluation(evalId);
    }

    /**
     * Supprime toutes les notes liées à un étudiant.
     */
    public int deleteNotesForStudent(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant est invalide pour la suppression");
        }
        return noteDao.deleteNotesForStudent(studentId);
    }

    /**
     * Récupère toutes les notes d'un étudiant.
     */
    public List<Note> getNotesForStudent(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant est invalide");
        }
        return noteDao.getNotesForStudent(studentId);
    }

    /**
     * Met à jour la note forcée pour un étudiant dans une évaluation.
     */
    public void updateForcedNoteForStudentEvaluation(long studentId, long evaluationId, double forcedNote) {
        if (studentId <= 0 || evaluationId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant ou de l'évaluation est invalide");
        }
        noteDao.updateForcedNoteForStudentEvaluation(studentId, evaluationId, forcedNote);
    }

    /**
     * Récupère une note pour un étudiant et une évaluation spécifique.
     */
    public Note getNoteForStudentEvaluation(long studentId, long evaluationId) {
        if (studentId <= 0 || evaluationId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant ou de l'évaluation est invalide");
        }
        return noteDao.getNoteForStudentEvaluation(studentId, evaluationId);
    }

    /**
     * Récupère toutes les notes pour les sous-évaluations d'une évaluation parent.
     */
    public List<Note> getNotesForChildEvaluations(long studentId, long parentEvaluationId) {
        if (studentId <= 0 || parentEvaluationId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant ou de l'évaluation parent est invalide");
        }
        return noteDao.getNotesForChildEvaluations(studentId, parentEvaluationId);
    }
}
