package com.example.projectv2_android.services;

import com.example.projectv2_android.models.Class;
import com.example.projectv2_android.repositories.ClassRepository;

import java.util.List;

public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    /**
     * Récupère toutes les classes (blocs)
     */
    public List<Class> getAllClasses() {
        return classRepository.getAllClasses();
    }

    /**
     * Ajoute une nouvelle classe
     */
    public long createClass(String name) {
        Class newClass = new Class(name);
        return classRepository.insertClass(newClass);
    }
}
