package com.example.projectv2_android.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projectv2_android.models.Evaluation;

import java.util.List;

@Dao
public interface EvaluationDao {
    @Insert
    long insertEvaluation(Evaluation evaluation);

    // Trouver une évaluation par ID
    @Query("SELECT * FROM Evaluation WHERE id = :id")
    Evaluation findById(long id);

    // Récupérer les évaluations d'une classe
    @Query("SELECT * FROM Evaluation WHERE classId = :classId")
    List<Evaluation> getEvaluationsForClass(long classId);

    // Récupérer les sous-évaluations d'une évaluation
    @Query("SELECT * FROM Evaluation WHERE parentId = :parentId")
    List<Evaluation> getChildEvaluations(long parentId);

    // Identifier les évaluations sans sous-évaluations (LeafEvaluation)
    @Query("SELECT * FROM Evaluation WHERE id NOT IN (SELECT DISTINCT parentId FROM Evaluation WHERE parentId IS NOT NULL)")
    List<Evaluation> getLeafEvaluations();

    // Identifier les évaluations avec des sous-évaluations (ParentEvaluation)
    @Query("SELECT * FROM Evaluation WHERE id IN (SELECT DISTINCT parentId FROM Evaluation WHERE parentId IS NOT NULL)")
    List<Evaluation> getParentEvaluations();
}
