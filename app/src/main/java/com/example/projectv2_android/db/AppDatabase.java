package com.example.projectv2_android.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.projectv2_android.dao.EvaluationDao;
import com.example.projectv2_android.dao.NoteDao;
import com.example.projectv2_android.dao.StudentDao;
import com.example.projectv2_android.dao.ClassDao;
import com.example.projectv2_android.dao.CourseDao; // Import du DAO des cours
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.models.Class;
import com.example.projectv2_android.models.Course; // Import du modèle des cours

@Database(entities = {Evaluation.class, Note.class, Student.class, Class.class, Course.class}, version = 2) // Ajout de Course
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract EvaluationDao evaluationDao();
    public abstract NoteDao noteDao();
    public abstract StudentDao studentDao();
    public abstract ClassDao classDao();
    public abstract CourseDao courseDao(); // Déclaration du DAO des cours

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "points_manager_db")
                    .fallbackToDestructiveMigration() // Migration destructive si structure changée
                    .build();
        }
        return INSTANCE;
    }
}
