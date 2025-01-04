package com.example.projectv2_android.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projectv2_android.models.Student;

import java.util.List;

@Dao
public interface StudentDao {

    @Insert
    long insertStudent(Student student);

    @Query("SELECT * FROM Student WHERE id = :id")
    Student findById(long id);

    @Query("SELECT * FROM Student WHERE class_id = :classId")
    List<Student> getStudentsForClass(long classId);

    @Query("DELETE FROM Student WHERE id = :id")
    int deleteStudent(long id);

    @Query("DELETE FROM Student WHERE class_id = :classId")
    int deleteStudentsForClass(long classId);
}
