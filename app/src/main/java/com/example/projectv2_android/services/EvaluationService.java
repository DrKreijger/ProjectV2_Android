package com.example.projectv2_android.services;

import android.util.Log;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.models.ParentEvaluation;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.List;

public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final NoteRepository noteRepository;

    public EvaluationService(EvaluationRepository evaluationRepository, NoteRepository noteRepository) {
        if (evaluationRepository == null || noteRepository == null) {
            throw new IllegalArgumentException("Les repositories ne peuvent pas être null !");
        }
        this.evaluationRepository = evaluationRepository;
        this.noteRepository = noteRepository;
    }

    /**
     * Récupère toutes les évaluations pour une classe donnée.
     */
    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        if (classId <= 0) {
            throw new IllegalArgumentException("L'ID de la classe doit être supérieur à 0 !");
        }

        return evaluationRepository.getAllEvaluationsForClass(classId);
    }

    /**
     * Récupère les sous-évaluations d'une évaluation parent.
     */
    public List<Evaluation> getChildEvaluations(long parentId) {
        if (parentId <= 0) {
            throw new IllegalArgumentException("L'ID du parent doit être valide !");
        }

        return evaluationRepository.getChildEvaluations(parentId);
    }

    /**
     * Calcule la moyenne pondérée d'une évaluation (parent ou feuille).
     */
    public double calculateWeightedAverage(long evaluationId) {
        if (evaluationId <= 0) {
            throw new IllegalArgumentException("L'ID de l'évaluation doit être valide !");
        }

        Evaluation evaluation = evaluationRepository.findById(evaluationId);
        if (evaluation == null) {
            Log.e("EvaluationService", "Aucune évaluation trouvée avec l'ID : " + evaluationId);
            return 0.0;
        }

        if (evaluation instanceof ParentEvaluation) {
            List<Evaluation> children = evaluationRepository.getChildEvaluations(evaluationId);
            if (children == null || children.isEmpty()) {
                Log.w("EvaluationService", "L'évaluation parent ID " + evaluationId + " n'a pas d'enfants.");
                return 0.0;
            }

            double totalPoints = 0.0;
            double totalWeight = 0.0;

            for (Evaluation child : children) {
                double childScore = calculateWeightedAverage(child.getId());
                totalPoints += childScore * child.getPointsMax();
                totalWeight += child.getPointsMax();
            }

            return totalWeight == 0 ? 0.0 : totalPoints / totalWeight;
        } else if (evaluation instanceof LeafEvaluation) {
            return ((LeafEvaluation) evaluation).calculateOverallScore();
        }

        Log.w("EvaluationService", "Type d'évaluation inconnu pour l'ID : " + evaluationId);
        return 0.0;
    }


    public long createParentEvaluation(String name, long classId, int pointsMax, List<Evaluation> children, Long parentId) {
        ParentEvaluation parentEvaluation = new ParentEvaluation(name, classId, parentId, pointsMax, children);
        long parentIdGenerated = evaluationRepository.insertEvaluation(parentEvaluation);

        for (Evaluation child : children) {
            child.setParentId(parentIdGenerated);
            evaluationRepository.insertEvaluation(child);
            Log.d("EvaluationService", "Sous-évaluation ajoutée : " + child.getName() + " avec parentId : " + parentIdGenerated);
        }

        return parentIdGenerated;
    }


    public long createLeafEvaluation(String name, long classId, Long parentId, int pointsMax) {
        LeafEvaluation leafEvaluation = new LeafEvaluation(name, classId, parentId, pointsMax, noteRepository);
        return evaluationRepository.insertEvaluation(leafEvaluation);
    }




    /**
     * Récupère une évaluation par son ID.
     */
    public Evaluation getEvaluationById(long evaluationId) {
        if (evaluationId <= 0) {
            throw new IllegalArgumentException("L'ID de l'évaluation doit être valide !");
        }

        return evaluationRepository.findById(evaluationId);
    }
}
