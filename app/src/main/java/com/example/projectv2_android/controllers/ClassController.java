package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Class;
import com.example.projectv2_android.repositories.ClassRepository;

import java.util.List;

public class ClassController {
    private final ClassRepository classRepository;

    public ClassController(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<Class> getAllClasses() {
        return classRepository.getAllClasses();
    }

    public long createClass(String name) {
        Class cls = new Class();
        cls.setName(name);
        return classRepository.insertClass(cls);
    }

    public Class findClassById(long classId) {
        return classRepository.findById(classId);
    }

    public boolean deleteClass(long classId) {
        return classRepository.deleteClass(classId) > 0;
    }
}
