package com.example.projectv2_android.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projectv2_android.models.EvaluationEntity;

import java.util.List;

@Dao
public interface EvaluationDao {
    @Insert
    long insertEvaluation(EvaluationEntity evaluationEntity);

    // Trouver une évaluation par ID
    @Query("SELECT * FROM Evaluation WHERE id = :id")
    EvaluationEntity findById(long id);

    // Récupérer les évaluations d'une classe
    @Query("SELECT * FROM Evaluation WHERE class_id = :classId")
    List<EvaluationEntity> getEvaluationsForClass(long classId);

    // Récupérer les sous-évaluations d'une évaluation
    @Query("SELECT * FROM Evaluation WHERE parent_id = :parentId")
    List<EvaluationEntity> getChildEvaluations(long parentId);

    // Identifier les évaluations sans sous-évaluations (LeafEvaluation)
    @Query("SELECT * FROM evaluation WHERE is_leaf = 1")
    List<EvaluationEntity> getLeafEvaluations();

    @Query("SELECT * FROM evaluation WHERE is_leaf = 0")
    List<EvaluationEntity> getParentEvaluations();

}
