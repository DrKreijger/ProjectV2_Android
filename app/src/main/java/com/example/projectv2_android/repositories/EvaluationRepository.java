package com.example.projectv2_android.repositories;

import com.example.projectv2_android.dao.EvaluationDao;
import com.example.projectv2_android.models.Evaluation;
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
        return evaluationDao.insertEvaluation(evaluation);
    }

    public Evaluation findById(long id) {
        Evaluation eval = evaluationDao.findById(id);
        return mapEvaluation(eval);
    }

    public List<Evaluation> getAllEvaluationsForClass(long classId) {
        List<Evaluation> evaluations = evaluationDao.getEvaluationsForClass(classId);
        return mapEvaluations(evaluations);
    }

    public List<Evaluation> getChildEvaluations(long parentId) {
        List<Evaluation> evaluations = evaluationDao.getChildEvaluations(parentId);
        return mapEvaluations(evaluations);
    }

    public List<ParentEvaluation> getParentEvaluations() {
        List<Evaluation> evaluations = evaluationDao.getParentEvaluations();
        return mapToParentEvaluations(evaluations);
    }

    public List<LeafEvaluation> getLeafEvaluations() {
        List<Evaluation> evaluations = evaluationDao.getLeafEvaluations();
        return mapToLeafEvaluations(evaluations);
    }

    private Evaluation mapEvaluation(Evaluation eval) {
        if (eval == null) return null;
        if (isParentEvaluation(eval)) {
            return new ParentEvaluation(
                    eval.getName(),
                    eval.getClassId(),
                    eval.getParentId(),
                    eval.getPointsMax()
            );
        } else {
            return new LeafEvaluation(
                    eval.getName(),
                    eval.getClassId(),
                    eval.getParentId(),
                    eval.getPointsMax(),
                    noteRepository
            );
        }
    }

    private List<Evaluation> mapEvaluations(List<Evaluation> evaluations) {
        List<Evaluation> result = new ArrayList<>();
        for (Evaluation eval : evaluations) {
            result.add(mapEvaluation(eval));
        }
        return result;
    }

    private List<ParentEvaluation> mapToParentEvaluations(List<Evaluation> evaluations) {
        List<ParentEvaluation> result = new ArrayList<>();
        for (Evaluation eval : evaluations) {
            result.add(new ParentEvaluation(
                    eval.getName(),
                    eval.getClassId(),
                    eval.getParentId(),
                    eval.getPointsMax()
            ));
        }
        return result;
    }

    private List<LeafEvaluation> mapToLeafEvaluations(List<Evaluation> evaluations) {
        List<LeafEvaluation> result = new ArrayList<>();
        for (Evaluation eval : evaluations) {
            result.add(new LeafEvaluation(
                    eval.getName(),
                    eval.getClassId(),
                    eval.getParentId(),
                    eval.getPointsMax(),
                    noteRepository
            ));
        }
        return result;
    }

    private boolean isParentEvaluation(Evaluation eval) {
        List<Evaluation> children = evaluationDao.getChildEvaluations(eval.getId());
        return !children.isEmpty();
    }
}
