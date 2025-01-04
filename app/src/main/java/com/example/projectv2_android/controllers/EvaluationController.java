package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Evaluation;
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

    public long createEvaluation(String name, long classId, Integer pointsMax, boolean hasSubEvaluations, Long parentId) {
        return evaluationService.createEvaluation(name, classId, pointsMax, hasSubEvaluations, parentId);
    }
}
