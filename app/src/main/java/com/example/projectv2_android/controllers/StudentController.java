package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.StudentRepository;

import java.util.List;

public class StudentController {
    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudentsForClass(long classId) {
        return studentRepository.getStudentsForClass(classId);
    }

    public long createStudent(String name, String firstName, String matricule, long classId) {
        Student student = new Student();
        student.setName(name);
        student.setFirstName(firstName);
        student.setMatricule(matricule);
        student.setClassId(classId);
        return studentRepository.insertStudent(student);
    }

    public boolean deleteStudent(long studentId) {
        return studentRepository.deleteStudent(studentId) > 0;
    }
}
