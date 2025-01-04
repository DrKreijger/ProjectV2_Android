package com.example.projectv2_android.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projectv2_android.models.Class;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    long insertClass(Class cls);

    @Query("SELECT * FROM Class WHERE id = :id")
    Class findById(long id);

    @Query("SELECT * FROM Class")
    List<Class> getAllClasses();

    @Query("DELETE FROM Class WHERE id = :id")
    int deleteClass(long id);
}

