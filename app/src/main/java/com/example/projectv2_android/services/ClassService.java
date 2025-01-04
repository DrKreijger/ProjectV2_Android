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
     * Récupère toutes les classes (blocs).
     */
    public List<Class> getAllClasses() {
        return classRepository.getAllClasses();
    }

    /**
     * Ajoute une nouvelle classe après vérification.
     */
    public long createClass(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe ne peut pas être vide.");
        }
        Class newClass = new Class(name.trim());
        return classRepository.insertClass(newClass);
    }

    /**
     * Trouve une classe par son ID.
     */
    public Class findClassById(long classId) {
        return classRepository.findById(classId);
    }

    /**
     * Supprime une classe.
     */
    public boolean deleteClass(long classId) {
        return classRepository.deleteClass(classId) > 0;
    }
}
