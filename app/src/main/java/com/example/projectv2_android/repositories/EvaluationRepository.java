package com.example.projectv2_android.repositories;

import com.example.projectv2_android.dao.EvaluationDao;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.EvaluationEntity;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.models.ParentEvaluation;

import java.util.ArrayList;
import java.util.List;

public class EvaluationRepository {
    private final EvaluationDao evaluationDao;
    private final NoteRepository noteRepository;

    public EvaluationRepository(EvaluationDao evaluationDao, NoteRepository noteRepository) {
        this.evaluationDao = evaluationDao;
        this.noteRepository = noteRepository;
    }

    public long insertEvaluation(Evaluation evaluation) {
        EvaluationEntity entity = mapToEntity(evaluation);
        long id = evaluationDao.insertEvaluation(entity); // La méthode insert doit renvoyer un ID non nul
        return id;
    }


    public Evaluation findById(long id) {
        EvaluationEntity entity = evaluationDao.findById(id);
        return mapToEvaluation(entity);
    }

    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        List<EvaluationEntity> entities = evaluationDao.getEvaluationsForClass(classId);
        return mapToEvaluations(entities);
    }


    public List<Evaluation> getChildEvaluations(long parentId) {
        List<EvaluationEntity> entities = evaluationDao.getChildEvaluations(parentId);
        return mapToEvaluations(entities);
    }


    public List<ParentEvaluation> getParentEvaluations() {
        List<EvaluationEntity> entities = evaluationDao.getParentEvaluations();
        return mapToParentEvaluations(entities);
    }

    public List<LeafEvaluation> getLeafEvaluations() {
        List<EvaluationEntity> entities = evaluationDao.getLeafEvaluations();
        return mapToLeafEvaluations(entities);
    }

    private Evaluation mapToEvaluation(EvaluationEntity entity) {
        if (entity == null) return null;

        if (isParentEvaluation(entity)) {
            List<Evaluation> children = mapToEvaluations(evaluationDao.getChildEvaluations(entity.getId()));
            ParentEvaluation parentEvaluation = new ParentEvaluation(
                    entity.getName(),
                    entity.getClassId(),
                    entity.getParentId(),
                    entity.getPointsMax(),
                    children
            );
            parentEvaluation.setId(entity.getId()); // Assurez-vous de définir l'ID ici
            return parentEvaluation;
        } else {
            LeafEvaluation leafEvaluation = new LeafEvaluation(
                    entity.getName(),
                    entity.getClassId(),
                    entity.getParentId(),
                    entity.getPointsMax(),
                    noteRepository
            );
            leafEvaluation.setId(entity.getId()); // Assurez-vous de définir l'ID ici
            return leafEvaluation;
        }
    }


    private List<Evaluation> mapToEvaluations(List<EvaluationEntity> entities) {
        List<Evaluation> evaluations = new ArrayList<>();
        for (EvaluationEntity entity : entities) {
            evaluations.add(mapToEvaluation(entity));
        }
        return evaluations;
    }

    private List<ParentEvaluation> mapToParentEvaluations(List<EvaluationEntity> entities) {
        List<ParentEvaluation> parentEvaluations = new ArrayList<>();
        for (EvaluationEntity entity : entities) {
            List<Evaluation> children = mapToEvaluations(evaluationDao.getChildEvaluations(entity.getId()));
            parentEvaluations.add(new ParentEvaluation(
                    entity.getName(),
                    entity.getClassId(),
                    entity.getParentId(),
                    entity.getPointsMax(),
                    children // Fournir les enfants obligatoirement
            ));
        }
        return parentEvaluations;
    }


    private List<LeafEvaluation> mapToLeafEvaluations(List<EvaluationEntity> entities) {
        List<LeafEvaluation> leafEvaluations = new ArrayList<>();
        for (EvaluationEntity entity : entities) {
            leafEvaluations.add(new LeafEvaluation(
                    entity.getName(),
                    entity.getClassId(),
                    entity.getParentId(),
                    entity.getPointsMax(),
                    noteRepository
            ));
        }
        return leafEvaluations;
    }

    private EvaluationEntity mapToEntity(Evaluation evaluation) {
        if (evaluation == null) return null;
        return new EvaluationEntity(
                evaluation.getName(),
                evaluation.getClassId(),
                evaluation.getParentId(),
                evaluation.getPointsMax(),
                evaluation.isLeaf()
        );
    }

    private boolean isParentEvaluation(EvaluationEntity entity) {
        List<EvaluationEntity> children = evaluationDao.getChildEvaluations(entity.getId());
        return !children.isEmpty();
    }

    public List<Evaluation> getAllEvaluationsForStudent(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant doit être valide !");
        }

        List<EvaluationEntity> evaluationEntities = evaluationDao.getEvaluationsForStudent(studentId);

        return mapToEvaluations(evaluationEntities);
    }


}
