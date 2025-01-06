package com.example.projectv2_android.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "evaluation",
        foreignKeys = {
                @ForeignKey(
                        entity = EvaluationEntity.class,
                        parentColumns = "id",
                        childColumns = "parent_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Class.class,
                        parentColumns = "id",
                        childColumns = "class_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class EvaluationEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "parent_id")
    private Long parentId;

    @ColumnInfo(name = "class_id")
    private long classId;

    @ColumnInfo(name = "points_max")
    private int pointsMax;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_leaf") // Nouveau champ pour simplifier la distinction feuille/parent
    private boolean isLeaf;

    public EvaluationEntity() {
    }

    public EvaluationEntity(String name, long classId, Long parentId, int pointsMax, boolean isLeaf) {
        this.name = name;
        this.classId = classId;
        this.parentId = parentId;
        this.pointsMax = pointsMax;
        this.isLeaf = isLeaf;
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public int getPointsMax() {
        return pointsMax;
    }

    public void setPointsMax(int pointsMax) {
        this.pointsMax = pointsMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }
}

