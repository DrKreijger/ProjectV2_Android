package com.example.projectv2_android.models;

public abstract class Evaluation {
    private long id;
    private Long parentId;
    private long classId;
    private int pointsMax;
    private String name;
    private boolean isLeaf; // Nouveau champ pour définir si c'est une feuille

    // Constructeur principal
    public Evaluation(String name, long classId, Long parentId, Integer pointsMax, boolean isLeaf) {
        this.name = name;
        this.classId = classId;
        this.parentId = parentId;
        this.pointsMax = pointsMax != null ? pointsMax : 20; // Par défaut, 20 points max
        this.isLeaf = isLeaf; // Définit si c'est une feuille
    }

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

    // Méthodes abstraites
    public abstract double calculateScoreForStudent(long studentId);
}
