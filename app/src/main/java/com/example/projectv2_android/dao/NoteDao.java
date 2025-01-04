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

    @Query("SELECT * FROM Note WHERE evalId = :evalId AND studentId = :studentId")
    Note getNoteForStudent(long evalId, long studentId);

    @Query("SELECT * FROM Note WHERE evalId = :evalId")
    List<Note> getNotesForEvaluation(long evalId);

    @Query("DELETE FROM Note WHERE evalId = :evalId")
    int deleteNotesForEvaluation(long evalId);

    @Query("DELETE FROM Note WHERE studentId = :studentId")
    int deleteNotesForStudent(long studentId);
}

