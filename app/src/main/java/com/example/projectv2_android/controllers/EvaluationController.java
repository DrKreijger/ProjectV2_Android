package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.models.ParentEvaluation;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;
import java.util.stream.Collectors;

public class EvaluationController {
    private final EvaluationRepository evaluationRepository;
    private final NoteRepository noteRepository;

    public EvaluationController(EvaluationRepository evaluationRepository, NoteRepository noteRepository) {
        this.evaluationRepository = evaluationRepository;
        this.noteRepository = noteRepository;
    }

    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        return evaluationRepository.getAllEvaluationsForClass(classId);
    }

    public List<Evaluation> getChildEvaluations(long parentId) {
        return evaluationRepository.getChildEvaluations(parentId);
    }

    public long createEvaluation(String name, long classId, Integer pointsMax, boolean hasSubEvaluations, Long parentId) {
        if (parentId == null && !hasSubEvaluations) {
            // Évaluation racine sans sous-évaluations
            LeafEvaluation evaluation = new LeafEvaluation(name, classId, null, pointsMax, noteRepository);
            return evaluationRepository.insertEvaluation(evaluation);
        } else if (parentId == null && hasSubEvaluations) {
            // Évaluation racine avec sous-évaluations
            ParentEvaluation evaluation = new ParentEvaluation(name, classId, null, pointsMax);
            return evaluationRepository.insertEvaluation(evaluation);
        } else {
            // Sous-évaluation
            LeafEvaluation subEvaluation = new LeafEvaluation(name, classId, parentId, pointsMax, noteRepository);
            return evaluationRepository.insertEvaluation(subEvaluation);
        }
    }

    public List<Evaluation> getParentEvaluations() {
        return evaluationRepository.getParentEvaluations()
                .stream()
                .map(e -> (Evaluation) e)
                .collect(Collectors.toList());
    }

    public List<Evaluation> getLeafEvaluations() {
        return evaluationRepository.getLeafEvaluations()
                .stream()
                .map(e -> (Evaluation) e)
                .collect(Collectors.toList());
    }
}
