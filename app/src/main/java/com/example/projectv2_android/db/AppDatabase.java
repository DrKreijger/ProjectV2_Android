package com.example.projectv2_android.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import com.example.projectv2_android.Converters;
import com.example.projectv2_android.dao.EvaluationDao;
import com.example.projectv2_android.dao.NoteDao;
import com.example.projectv2_android.dao.StudentDao;
import com.example.projectv2_android.dao.ClassDao;
import com.example.projectv2_android.models.EvaluationEntity; // Utilisation d'EvaluationEntity
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.models.Class;

@Database(entities = {EvaluationEntity.class, Note.class, Student.class, Class.class}, version = 7) // Suppression de Course
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract EvaluationDao evaluationDao();
    public abstract NoteDao noteDao();
    public abstract StudentDao studentDao();
    public abstract ClassDao classDao();

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
