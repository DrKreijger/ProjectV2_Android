package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.models.ParentEvaluation;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;

public class EvaluationService {
    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        return evaluationRepository.getAllEvaluationsForClass(classId);
    }

    public List<Evaluation> getChildEvaluations(long parentId) {
        return evaluationRepository.getChildEvaluations(parentId);
    }

    public double calculateWeightedAverage(long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId);

        if (evaluation instanceof ParentEvaluation) {
            List<Evaluation> children = evaluationRepository.getChildEvaluations(evaluationId);
            double totalPoints = 0.0;
            double totalWeight = 0.0;

            for (Evaluation child : children) {
                totalPoints += calculateWeightedAverage(child.getId()) * child.getPointsMax();
                totalWeight += child.getPointsMax();
            }

            return totalWeight == 0 ? 0.0 : totalPoints / totalWeight;
        } else if (evaluation instanceof LeafEvaluation) {
            // Si c'est une feuille, renvoyer la moyenne des notes
            return ((LeafEvaluation) evaluation).calculateOverallScore();
        }

        return 0.0;
    }

    public long createEvaluation(String name, long classId, Integer pointsMax, boolean hasSubEvaluations, Long parentId) {
        if (hasSubEvaluations) {
            ParentEvaluation parentEvaluation = new ParentEvaluation(name, classId, parentId, pointsMax);
            return evaluationRepository.insertEvaluation(parentEvaluation);
        } else {
            LeafEvaluation leafEvaluation = new LeafEvaluation(name, classId, parentId, pointsMax, null); // NoteRepository non nécessaire ici
            return evaluationRepository.insertEvaluation(leafEvaluation);
        }
    }
}
