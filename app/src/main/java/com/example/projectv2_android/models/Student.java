package com.example.projectv2_android.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Student",
        foreignKeys = @ForeignKey(
                entity = Class.class,
                parentColumns = "id",
                childColumns = "class_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class Student {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "classId")
    private long classId; // ID de la classe de l'étudiant

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "matricule")
    private String matricule;

    // Constructeur par défaut requis par Room
    public Student() {
    }

    // Constructeur complet
    public Student(String name, String firstName, String matricule, long classId) {
        this.name = name;
        this.firstName = firstName;
        this.matricule = matricule;
        this.classId = classId;
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
}
