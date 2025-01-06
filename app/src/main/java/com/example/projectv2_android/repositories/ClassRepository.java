package com.example.projectv2_android.repositories;

import com.example.projectv2_android.dao.ClassDao;
import com.example.projectv2_android.models.Class;

import java.util.List;

public class ClassRepository {
    private final ClassDao classDao;

    public ClassRepository(ClassDao classDao) {
        this.classDao = classDao;
    }

    public long insertClass(Class cls) {
        return classDao.insertClass(cls);
    }

    public Class findById(long id) {
        return classDao.findById(id);
    }

    public List<Class> getAllClasses() {
        return classDao.getAllClasses();
    }

    public String getClassNameById(long classId) {
        return classDao.getClassNameById(classId); // Méthode dans ClassDao
    }

    public int deleteClass(long id) {
        return classDao.deleteClass(id);
    }
}

