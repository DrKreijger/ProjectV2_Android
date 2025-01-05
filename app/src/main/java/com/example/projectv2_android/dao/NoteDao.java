package com.example.projectv2_android.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectv2_android.models.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insertNote(Note note);

    @Update
    int updateNote(Note note);

    @Query("SELECT * FROM Note WHERE eval_id = :evalId AND student_id = :studentId")
    Note getNoteForStudent(long evalId, long studentId);

    @Query("SELECT * FROM Note WHERE eval_id = :evalId")
    List<Note> getNotesForEvaluation(long evalId);

    @Query("DELETE FROM Note WHERE eval_id = :evalId")
    int deleteNotesForEvaluation(long evalId);

    @Query("DELETE FROM Note WHERE student_id = :studentId")
    int deleteNotesForStudent(long studentId);

    @Query("SELECT * FROM Note WHERE student_id = :studentId")
    List<Note> getNotesForStudent(long studentId);

    @Query("UPDATE Note SET forced_value = :forcedNote WHERE student_id = :studentId AND eval_id = :evaluationId")
    void updateForcedNoteForStudentEvaluation(long studentId, long evaluationId, double forcedNote);

    @Query("SELECT * FROM Note WHERE student_id = :studentId AND eval_id IN (SELECT id FROM Evaluation WHERE parent_id = :parentEvaluationId)")
    List<Note> getNotesForChildEvaluations(long studentId, long parentEvaluationId);

    @Query("SELECT * FROM Note WHERE student_id = :studentId AND eval_id = :evaluationId")
    Note getNoteForStudentEvaluation(long studentId, long evaluationId);

}
