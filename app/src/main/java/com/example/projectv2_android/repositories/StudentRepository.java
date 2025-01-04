package com.example.projectv2_android.repositories;

import com.example.projectv2_android.dao.StudentDao;
import com.example.projectv2_android.models.Student;

import java.util.List;

public class StudentRepository {
    private final StudentDao studentDao;

    public StudentRepository(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public long insertStudent(Student student) {
        return studentDao.insertStudent(student);
    }

    public Student findById(long id) {
        return studentDao.findById(id);
    }

    public List<Student> getStudentsForClass(long classId) {
        return studentDao.getStudentsForClass(classId);
    }

    public int deleteStudent(long id) {
        return studentDao.deleteStudent(id);
    }

    public int deleteStudentsForClass(long classId) {
        return studentDao.deleteStudentsForClass(classId);
    }
}
