package com.example.projectv2_android.controllers;

import com.example.projectv2_android.models.Class;
import com.example.projectv2_android.services.ClassService;

import java.util.List;

public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    /**
     * Récupère toutes les classes (blocs).
     */
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    /**
     * Ajoute une nouvelle classe.
     */
    public long addClass(String name) {
        return classService.createClass(name);
    }

    /**
     * Trouve une classe par son ID.
     */
    public Class getClassById(long classId) {
        return classService.findClassById(classId);
    }

    /**
     * Supprime une classe.
     */
    public boolean removeClass(long classId) {
        return classService.deleteClass(classId);
    }
}
