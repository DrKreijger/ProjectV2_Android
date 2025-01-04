package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.services.StudentService;

import java.util.List;

public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Récupère tous les étudiants d'une classe
     */
    public List<Student> getStudentsForClass(long classId) {
        if (classId <= 0) {
            throw new IllegalArgumentException("L'ID de la classe est invalide !");
        }
        return studentService.getStudentsForClass(classId);
    }

    /**
     * Crée un nouvel étudiant
     */
    public long createStudent(String firstName, String lastName, String matricule, long classId) {
        if (firstName == null || lastName == null || matricule == null) {
            throw new IllegalArgumentException("Les données de l'étudiant sont invalides !");
        }
        return studentService.createStudent(firstName, lastName, matricule, classId);
    }

    /**
     * Supprime un étudiant
     */
    public boolean deleteStudent(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("L'ID de l'étudiant est invalide !");
        }
        return studentService.deleteStudent(studentId);
    }

    public StudentService getStudentService() {
        return studentService;
    }
}
