package com.example.projectv2_android.models;

public abstract class Evaluation {
    private long id;
    private Long parentId;
    private long classId;
    private int pointsMax;
    private String name;

    public Evaluation(String name, long classId, Long parentId, Integer pointsMax) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (pointsMax == null || pointsMax <= 0) {
            throw new IllegalArgumentException("Le nombre de points maximum doit être positif");
        }
        this.name = name;
        this.classId = classId;
        this.parentId = parentId;
        this.pointsMax = pointsMax;
    }

    // Getters et Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public long getClassId() { return classId; }
    public void setClassId(long classId) { this.classId = classId; }

    public int getPointsMax() { return pointsMax; }
    public void setPointsMax(int pointsMax) { this.pointsMax = pointsMax; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Méthodes abstraites
    public abstract boolean isLeaf();
    public abstract double calculateScoreForStudent(long studentId);
}
