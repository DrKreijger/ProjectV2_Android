package com.example.projectv2_android.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Course",
        foreignKeys = @ForeignKey(entity = Class.class, parentColumns = "id", childColumns = "classId")
)
public class Course {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long classId; // Bloc auquel le cours appartient
    private String name;

    // Getters et Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getClassId() { return classId; }
    public void setClassId(long classId) { this.classId = classId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
