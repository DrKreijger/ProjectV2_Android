package com.example.projectv2_android.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.projectv2_android.models.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    long insertCourse(Course course);

    @Query("SELECT * FROM Course WHERE id = :id")
    Course findById(long id);

    @Query("SELECT * FROM Course WHERE classId = :classId")
    List<Course> getCoursesForClass(long classId);

    @Query("SELECT * FROM Course")
    List<Course> getAllCourses();

    @Query("DELETE FROM Course WHERE id = :id")
    int deleteCourse(long id);
}
