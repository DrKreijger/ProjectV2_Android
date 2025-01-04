package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.repositories.StudentRepository;

import java.util.List;

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
     * Récupère tous les étudiants d'une classe
     */
    public List<Student> getStudentsForClass(long classId) {
        return studentRepository.getStudentsForClass(classId);
    }

    /**
     * Vérifie si un matricule est unique dans une classe
     */
    private boolean isMatriculeUnique(String matricule, long classId) {
        List<Student> students = studentRepository.getStudentsForClass(classId);
        for (Student student : students) {
            if (student.getMatricule().equalsIgnoreCase(matricule)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ajoute un nouvel étudiant après vérification des règles métier
     */
    public long createStudent(String firstName, String lastName, String matricule, long classId) {
        if (firstName.isEmpty() || lastName.isEmpty() || matricule.isEmpty()) {
            throw new IllegalArgumentException("Tous les champs doivent être remplis !");
        }

        if (!isMatriculeUnique(matricule, classId)) {
            throw new IllegalArgumentException("Le matricule existe déjà dans cette classe !");
        }

        Student student = new Student(firstName, lastName, matricule, classId);
        return studentRepository.insertStudent(student);
    }

    /**
     * Supprime un étudiant
     */
    public boolean deleteStudent(long studentId) {
        return studentRepository.deleteStudent(studentId) > 0;
    }

    /**
     * Calcule la moyenne pondérée d'un étudiant.
     */
    public double calculateStudentAverage(long studentId) {
        List<Note> notes = noteRepository.getNotesForStudent(studentId);

        if (notes == null || notes.isEmpty()) {
            return 0;
        }

        double totalWeightedScores = 0;
        double totalWeights = 0;

        for (Note note : notes) {
            // Utilise la valeur forcée si elle existe, sinon la valeur normale
            double score = note.getForcedValue() != null ? note.getForcedValue() : note.getNoteValue();

            // Récupère les informations de l'évaluation associée
            Evaluation evaluation = evaluationRepository.findById(note.getEvalId());
            if (evaluation != null) {
                double weight = evaluation.getPointsMax();
                totalWeightedScores += score * weight;
                totalWeights += weight;
            }
        }

        if (totalWeights == 0) {
            return 0;
        }

        // Calcul de la moyenne pondérée
        double average = totalWeightedScores / totalWeights;

        // Arrondi à 0,5 près
        return Math.round(average * 2) / 2.0;
    }
}
