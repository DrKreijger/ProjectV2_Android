package com.example.projectv2_android.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectv2_android.fragments.EvaluationsListFragment;
import com.example.projectv2_android.fragments.StudentsListFragment;

public class ClassTabsAdapter extends FragmentStateAdapter {

    private final long classId;

    public ClassTabsAdapter(@NonNull Fragment fragment, long classId) {
        super(fragment);
        this.classId = classId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return EvaluationsListFragment.newInstance(classId);
        } else {
            return StudentsListFragment.newInstance(classId);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Deux onglets : Évaluations et Étudiants
    }
}
