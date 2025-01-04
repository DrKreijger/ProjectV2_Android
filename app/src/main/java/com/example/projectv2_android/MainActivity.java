package com.example.projectv2_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.projectv2_android.fragments.ClassesListFragment;
import com.example.projectv2_android.fragments.EvaluationsListFragment;
import com.example.projectv2_android.fragments.StudentsListFragment;
import com.example.projectv2_android.fragments.StudentDetailFragment;

public class MainActivity extends AppCompatActivity
        implements ClassesListFragment.ClassesListNavigator,
        StudentsListFragment.StudentsListNavigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Affiche la liste des classes par défaut
            ClassesListFragment fragment = new ClassesListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void openStudentsList(long classId) {
        StudentsListFragment fragment = StudentsListFragment.newInstance(classId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openEvaluationsList(long classId) {
        EvaluationsListFragment fragment = EvaluationsListFragment.newInstance(classId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void openStudentDetail(long studentId, long classId) {
        StudentDetailFragment fragment = StudentDetailFragment.newInstance(studentId, classId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
