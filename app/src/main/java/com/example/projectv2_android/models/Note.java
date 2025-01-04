package com.example.projectv2_android.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "Note",
        foreignKeys = {
                @ForeignKey(
                        entity = Evaluation.class,
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
    private long id;
    private long evalId;
    private long studentId;
    private double noteValue;
    private Double forcedValue; // Nullable for forced notes

    // Getters and Setters
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

    public double getNoteValue() {
        return noteValue;
    }

    public void setNoteValue(double noteValue) {
        this.noteValue = noteValue;
    }

    public Double getForcedValue() {
        return forcedValue;
    }

    public void setForcedValue(Double forcedValue) {
        this.forcedValue = forcedValue;
    }
}
