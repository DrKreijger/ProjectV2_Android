package com.example.projectv2_android.models;

import java.util.List;

public class ParentEvaluation extends Evaluation {
    private List<Evaluation> children;

    public ParentEvaluation(String name, long classId, Long parentId, Integer pointsMax) {
        super(name, classId, parentId, pointsMax);
    }

    public List<Evaluation> getChildren() {
        return children;
    }

    public void setChildren(List<Evaluation> children) {
        this.children = children;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public double calculateScoreForStudent(long studentId) {
        double totalScore = 0.0;
        double totalMaxPoints = 0.0;

        if (children != null) {
            for (Evaluation child : children) {
                totalScore += child.calculateScoreForStudent(studentId);
                totalMaxPoints += child.getPointsMax();
            }
        }

        return totalMaxPoints == 0 ? 0.0 : (totalScore / totalMaxPoints) * getPointsMax();
    }
}

