package com.example.projectv2_android.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.models.ParentEvaluation;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.repositories.StudentRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class StudentService {
    private final StudentRepository studentRepository;
    private final EvaluationRepository evaluationRepository;
    private final NoteRepository noteRepository;

    public StudentService(StudentRepository studentRepository, EvaluationRepository evaluationRepository, NoteRepository noteRepository) {
        this.studentRepository = studentRepository;
        this.evaluationRepository = evaluationRepository;
        this.noteRepository = noteRepository;
    }

    /**
     * Récupère tous les étudiants d'une classe.
     */
    public List<Student> getStudentsForClass(long classId) {
        return studentRepository.getStudentsForClass(classId);
    }

    /**
     * Ajoute un nouvel étudiant après vérification des règles métier
     * et l'inscrit automatiquement à toutes les évaluations de la classe.
     */
    public long createStudent(String firstName, String lastName, String matricule, long classId) {
        if (firstName.isEmpty() || lastName.isEmpty() || matricule.isEmpty()) {
            throw new IllegalArgumentException("Tous les champs doivent être remplis !");
        }

        List<Student> students = studentRepository.getStudentsForClass(classId);
        for (Student student : students) {
            if (student.getMatricule().equalsIgnoreCase(matricule)) {
                throw new IllegalArgumentException("Le matricule existe déjà dans cette classe !");
            }
        }

        Student student = new Student(firstName, lastName, matricule, classId);
        long studentId = studentRepository.insertStudent(student);

        // Inscription automatique aux évaluations
        enrollStudentInEvaluations(studentId, classId);

        return studentId;
    }

    /**
     * Inscrit un étudiant à toutes les évaluations d'une classe.
     */
    public void enrollStudentInEvaluations(long studentId, long classId) {
        List<Evaluation> evaluations = evaluationRepository.getAllEvaluationsForClass(classId);
        for (Evaluation evaluation : evaluations) {
            Note existingNote = noteRepository.getNoteForStudentEvaluation(studentId, evaluation.getId());
            if (existingNote == null) {
                Note note = new Note();
                note.setStudentId(studentId);
                note.setEvalId(evaluation.getId());
                note.setNoteValue(null); // Note par défaut "non notée"
                noteRepository.insertNote(note);
            }
        }
    }

    /**
     * Supprime un étudiant.
     */
    public boolean deleteStudent(long studentId) {
        return studentRepository.deleteStudent(studentId) > 0;
    }

    /**
     * Calcule la moyenne générale pondérée d'un étudiant pour toutes les évaluations.
     */
    public void calculateStudentAverage(long studentId, Callback<Double> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Evaluation> evaluations = evaluationRepository.getAllEvaluationsForStudent(studentId);
                Log.d("StudentService", "Nombre d'évaluations associées à l'étudiant : " + evaluations.size());

                double weightedAverage = calculateWeightedAverageForEvaluations(studentId, evaluations);

                double roundedAverage = roundToHalf(weightedAverage);
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(roundedAverage));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }
        });
    }


    /**
     * Calcule la moyenne pondérée pour une liste d'évaluations.
     * Prend en compte les notes forcées.
     */
    private double calculateWeightedAverageForEvaluations(long studentId, List<Evaluation> evaluations) {
        double totalWeightedScore = 0;
        double totalWeight = 0;

        for (Evaluation evaluation : evaluations) {
            Log.d("StudentService", "Traitement de l'évaluation : " + evaluation.getName() + " | ID : " + evaluation.getId());

            // Ne pas inclure les sous-évaluations dans le calcul
            if (!isSubEvaluation(evaluation, evaluations)) {
                double score = calculateScoreForEvaluation(studentId, evaluation.getId());
                double weight = evaluation.getPointsMax();

                if (weight > 0) {
                    Log.d("StudentService", "Évaluation incluse : " + evaluation.getName() + " | Score : " + score + " | Poids : " + weight);
                    totalWeightedScore += score * weight;
                    totalWeight += weight;
                } else {
                    Log.d("StudentService", "Évaluation ignorée (poids nul) : " + evaluation.getName());
                }
            } else {
                Log.d("StudentService", "Sous-évaluation ignorée : " + evaluation.getName());
            }
        }

        // Log pour valider les calculs finaux
        Log.d("StudentService", "TOTAL -> TotalWeightedScore : " + totalWeightedScore + " | TotalWeight : " + totalWeight);

        return totalWeight > 0 ? roundToNearestHalf(totalWeightedScore / totalWeight) : 0;
    }



    /**
     * Calcule la note d'un étudiant pour une évaluation spécifique, en tenant compte des sous-évaluations et des notes forcées.
     */
    /**
     * Calcule la note d'un étudiant pour une évaluation spécifique, en tenant compte des sous-évaluations et des notes forcées.
     */
    public double calculateScoreForEvaluation(long studentId, long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId);

        if (evaluation == null) {
            Log.w("StudentService", "Évaluation introuvable pour ID : " + evaluationId);
            return 0;
        }

        List<Evaluation> subEvaluations = evaluationRepository.getChildEvaluations(evaluationId);

        if (subEvaluations.isEmpty()) {
            Note note = noteRepository.getNoteForStudentEvaluation(studentId, evaluationId);

            if (note != null) {
                if (note.getForcedValue() != null) {
                    Log.d("StudentService", "Évaluation feuille : " + evaluation.getName() + " | Note forcée : " + note.getForcedValue());
                    return note.getForcedValue();
                }

                if (note.getNoteValue() != null) {
                    Log.d("StudentService", "Évaluation feuille : " + evaluation.getName() + " | Note normale : " + note.getNoteValue());
                    return note.getNoteValue();
                }
            }

            Log.d("StudentService", "Évaluation feuille : " + evaluation.getName() + " | Pas de note.");
            return 0;
        } else {
            double sumScores = 0;
            double sumMax = 0;

            for (Evaluation subEvaluation : subEvaluations) {
                double score = calculateScoreForEvaluation(studentId, subEvaluation.getId());
                sumScores += score;
                sumMax += subEvaluation.getPointsMax();

                Log.d("StudentService", "Sous-évaluation : " + subEvaluation.getName() + " | Score : " + score + " | Points Max : " + subEvaluation.getPointsMax());
            }

            // Vérifier si une note forcée est présente sur l'évaluation parent
            Note parentNote = noteRepository.getNoteForStudentEvaluation(studentId, evaluationId);
            if (parentNote != null && parentNote.getForcedValue() != null) {
                Log.d("StudentService", "Évaluation parent : " + evaluation.getName() + " | Note forcée : " + parentNote.getForcedValue());
                return parentNote.getForcedValue();
            }

            // Calculer le score de l'évaluation parent en fonction des sous-évaluations
            if (sumMax > 0) {
                double parentScore = (sumScores / sumMax) * evaluation.getPointsMax();
                Log.d("StudentService", "Évaluation parent : " + evaluation.getName() + " | Score Calculé : " + parentScore);
                return parentScore;
            } else {
                Log.d("StudentService", "Évaluation parent : " + evaluation.getName() + " | Points Max des sous-évaluations est 0.");
                return 0;
            }
        }
    }




    private boolean isSubEvaluation(Evaluation evaluation, List<Evaluation> evaluations) {
        if (evaluation == null || evaluations == null || evaluations.isEmpty()) {
            Log.d("StudentService", "Échec de détection de sous-évaluation : la liste ou l'évaluation est invalide.");
            return false;
        }

        Log.d("StudentService", "Début de vérification pour : " + evaluation.getName() + " | ID : " + evaluation.getId());

        for (Evaluation parent : evaluations) {
            if (parent instanceof ParentEvaluation) {
                List<Evaluation> children = ((ParentEvaluation) parent).getChildren();

                if (children == null || children.isEmpty()) {
                    Log.d("StudentService", "Pas de sous-évaluations pour le parent : " + parent.getName() + " | ID : " + parent.getId());
                    continue;
                }

                for (Evaluation child : children) {
                    Log.d("StudentService", "Comparaison : " + child.getName() + " (ID : " + child.getId() + ") avec " + evaluation.getName());
                    if (child.getId() == evaluation.getId()) {
                        Log.d("StudentService", "Sous-évaluation détectée : " + evaluation.getName() + " appartient à " + parent.getName());
                        return true;
                    }
                }
            }
        }

        Log.d("StudentService", "L'évaluation " + evaluation.getName() + " n'est pas une sous-évaluation.");
        return false;
    }



    /**
     * Forcer une note pour une évaluation spécifique d'un étudiant.
     */
    public void forceNoteForEvaluation(long studentId, long evaluationId, double forcedValue, Callback<Boolean> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Note note = noteRepository.getNoteForStudentEvaluation(studentId, evaluationId);
                if (note == null) {
                    note = new Note();
                    note.setStudentId(studentId);
                    note.setEvalId(evaluationId);
                }
                note.setForcedValue(forcedValue);

                noteRepository.insertOrUpdate(note);

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(true));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }
        });
    }

    /**
     * Ajoute ou met à jour une note pour une évaluation spécifique d'un étudiant.
     */
    public void addOrUpdateNoteForEvaluation(long studentId, long evaluationId, double noteValue, Callback<Boolean> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Note note = noteRepository.getNoteForStudentEvaluation(studentId, evaluationId);
                if (note == null) {
                    note = new Note();
                    note.setStudentId(studentId);
                    note.setEvalId(evaluationId);
                }
                note.setNoteValue(noteValue);

                noteRepository.insertOrUpdate(note);

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(true));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }
        });
    }

    /**
     * Arrondi une valeur à 0,5 près.
     */
    private double roundToHalf(double value) {
        return Math.round(value * 2) / 2.0;
    }

    private double roundToNearestHalf(double value) {
        return Math.round(value * 2) / 2.0;
    }

    /**
     * Interface Callback pour les retours asynchrones.
     */
    public interface Callback<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }
}
