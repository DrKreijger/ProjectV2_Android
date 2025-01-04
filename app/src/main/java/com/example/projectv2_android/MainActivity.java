package com.example.projectv2_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.projectv2_android.fragments.ClassesListFragment;
import com.example.projectv2_android.fragments.EvaluationsListFragment;
import com.example.projectv2_android.fragments.StudentsListFragment;
import com.example.projectv2_android.fragments.StudentDetailFragment;
import com.example.projectv2_android.fragments.EvaluationDetailFragment;

public class MainActivity extends AppCompatActivity
        implements ClassesListFragment.ClassesListNavigator,
        EvaluationsListFragment.EvaluationsListNavigator,
        StudentsListFragment.StudentsListNavigator,
        EvaluationDetailFragment.EvaluationDetailNavigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Load the starting fragment: ClassesListFragment
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
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openEvaluationsList(long classId) {
        EvaluationsListFragment fragment = EvaluationsListFragment.newInstance(classId);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openEvaluationDetail(long evalId) {
        EvaluationDetailFragment fragment = EvaluationDetailFragment.newInstance(evalId);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openStudentDetail(long studentId, long evalId) {
        StudentDetailFragment fragment = StudentDetailFragment.newInstance(studentId, evalId);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
