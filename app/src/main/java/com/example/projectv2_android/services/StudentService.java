package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
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
    public void calculateStudentAverage(long studentId, Callback<Double> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Appel Room dans un thread séparé
                List<Note> notes = noteRepository.getNotesForStudent(studentId);
                double average = calculateAverageFromNotes(notes);

                // Retour des résultats sur le thread principal
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(average);
                });
            } catch (Exception e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e);
                });
            }
        });
    }

    private double calculateAverageFromNotes(List<Note> notes) {
        if (notes == null || notes.isEmpty()) return 0;
        double sum = 0;
        for (Note note : notes) {
            sum += note.getNoteValue(); // Exemple de calcul
        }
        return sum / notes.size();
    }

    // Interface Callback pour les retours
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

}
