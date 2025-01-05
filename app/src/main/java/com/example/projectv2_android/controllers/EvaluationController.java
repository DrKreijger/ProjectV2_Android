package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.models.ParentEvaluation;
import com.example.projectv2_android.services.EvaluationService;

import java.util.List;

public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        return evaluationService.getAllEvaluationsForClass(classId);
    }

    public List<Evaluation> getChildEvaluations(long parentId) {
        return evaluationService.getChildEvaluations(parentId);
    }

    public double calculateWeightedAverage(long evaluationId) {
        return evaluationService.calculateWeightedAverage(evaluationId);
    }

    public long createParentEvaluation(String name, long classId, int pointsMax, List<Evaluation> children, Long parentId) {
        return evaluationService.createParentEvaluation(name, classId, pointsMax, children, parentId);
    }

    public long createLeafEvaluation(String name, long classId, Long parentId, int pointsMax) {
        return evaluationService.createLeafEvaluation(name, classId, parentId, pointsMax);
    }


}
