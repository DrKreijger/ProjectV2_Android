package com.example.projectv2_android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Class")
public class Class {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name; // ex. "BA1", "BA2", etc.

    public Class() { }

    public Class(String name) {
        this.name = name;
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

