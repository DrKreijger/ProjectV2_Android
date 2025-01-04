package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.StudentRepository;

import java.util.List;

public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Récupère tous les étudiants d'une classe
     */
    public List<Student> getStudentsForClass(long classId) {
        return studentRepository.getStudentsForClass(classId);
    }

    /**
     * Ajoute un nouvel étudiant
     */
    public long createStudent(String firstName, String lastName, String matricule, long classId) {
        Student student = new Student(firstName, lastName, matricule, classId);
        return studentRepository.insertStudent(student);
    }
}
