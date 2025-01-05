package com.example.projectv2_android.models;

import java.util.Collections;
import java.util.List;

public class ParentEvaluation extends Evaluation {
    private final List<Evaluation> children; // Liste finale, définie dès la création

    public ParentEvaluation(String name, long classId, Long parentId, Integer pointsMax, List<Evaluation> children) {
        super(name, classId, parentId, pointsMax, false); // isLeaf = false
        if (children == null || children.isEmpty()) {
            throw new IllegalArgumentException("Une évaluation parent doit avoir au moins un enfant !");
        }
        this.children = Collections.unmodifiableList(children); // Liste immuable
    }

    public List<Evaluation> getChildren() {
        return children;
    }

    @Override
    public double calculateScoreForStudent(long studentId) {
        double totalScore = 0.0;
        double totalMaxPoints = 0.0;

        for (Evaluation child : children) {
            totalScore += child.calculateScoreForStudent(studentId);
            totalMaxPoints += child.getPointsMax();
        }

        return totalMaxPoints == 0 ? 0.0 : (totalScore / totalMaxPoints) * getPointsMax();
    }
}
