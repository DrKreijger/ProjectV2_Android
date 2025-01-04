package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.models.ParentEvaluation;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.ArrayList;
import java.util.List;

public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final NoteRepository noteRepository;

    public EvaluationService(EvaluationRepository evaluationRepository, NoteRepository noteRepository) {
        this.evaluationRepository = evaluationRepository;
        this.noteRepository = noteRepository;
    }

    /**
     * Récupère toutes les évaluations pour une classe donnée
     */
    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        return evaluationRepository.getAllEvaluationsForClass(classId);
    }

    /**
     * Récupère les sous-évaluations d'une évaluation parent
     */
    public List<Evaluation> getChildEvaluations(long parentId) {
        return evaluationRepository.getChildEvaluations(parentId);
    }

    /**
     * Calcule la moyenne pondérée des sous-évaluations
     */
    public double calculateWeightedAverage(long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId);

        if (evaluation instanceof ParentEvaluation) {
            List<Evaluation> children = evaluationRepository.getChildEvaluations(evaluationId);
            double totalPoints = 0.0;
            double totalWeight = 0.0;

            for (Evaluation child : children) {
                double childScore = calculateWeightedAverage(child.getId());
                totalPoints += childScore * child.getPointsMax();
                totalWeight += child.getPointsMax();
            }

            if (totalWeight == 0) return 0.0;
            return totalPoints / totalWeight;
        } else if (evaluation instanceof LeafEvaluation) {
            // Si c'est une feuille, retournez la note directe
            return ((LeafEvaluation) evaluation).calculateScoreForStudent(-1); // -1 pour une moyenne générale
        }

        return 0.0;
    }

    /**
     * Crée une nouvelle évaluation
     */
    public long createEvaluation(String name, long classId, Integer pointsMax, boolean hasSubEvaluations, Long parentId) {
        if (parentId == null && hasSubEvaluations) {
            ParentEvaluation parentEvaluation = new ParentEvaluation(name, classId, null, pointsMax);
            return evaluationRepository.insertEvaluation(parentEvaluation);
        } else {
            LeafEvaluation leafEvaluation = new LeafEvaluation(name, classId, parentId, pointsMax, noteRepository);
            return evaluationRepository.insertEvaluation(leafEvaluation);
        }
    }
}
