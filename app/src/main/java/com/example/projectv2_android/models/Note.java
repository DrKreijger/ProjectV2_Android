package com.example.projectv2_android.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Note",
        foreignKeys = {
                @ForeignKey(
                        entity = EvaluationEntity.class, // Utilisation de EvaluationEntity
                        parentColumns = "id",
                        childColumns = "eval_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Student.class,
                        parentColumns = "id",
                        childColumns = "student_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "eval_id")
    private long evalId;

    @ColumnInfo(name = "student_id")
    private long studentId;

    @ColumnInfo(name = "note_value")
    private Double noteValue;

    @ColumnInfo(name = "forced_value")
    private Double forcedValue; // Nullable for forced notes

    // Constructeur par défaut requis par Room
    public Note() {
    }

    // Constructeur principal
    public Note(long evalId, long studentId, double noteValue, Double forcedValue) {
        this.evalId = evalId;
        this.studentId = studentId;
        this.noteValue = noteValue;
        this.forcedValue = forcedValue;
    }

    // Getters et Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEvalId() {
        return evalId;
    }

    public void setEvalId(long evalId) {
        this.evalId = evalId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public Double getNoteValue() {
        return noteValue;
    }

    public void setNoteValue(Double noteValue) {
        this.noteValue = noteValue;
    }

    public Double getForcedValue() {
        return forcedValue;
    }

    public void setForcedValue(Double forcedValue) {
        this.forcedValue = forcedValue;
    }
}
