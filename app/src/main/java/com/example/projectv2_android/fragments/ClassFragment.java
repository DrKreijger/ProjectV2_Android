package com.example.projectv2_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectv2_android.R;
import com.example.projectv2_android.adapters.ClassTabsAdapter;
import com.example.projectv2_android.dialogs.AddEvaluationDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClassFragment extends Fragment {

    private static final String ARG_CLASS_ID = "class_id";
    private long classId;

    public static ClassFragment newInstance(long classId) {
        ClassFragment fragment = new ClassFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getLong(ARG_CLASS_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        // Configure l'adaptateur pour les onglets
        ClassTabsAdapter adapter = new ClassTabsAdapter(this, classId);
        viewPager.setAdapter(adapter);

        // Lier TabLayout et ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Évaluations");
            } else {
                tab.setText("Étudiants");
            }
        }).attach();

        // Bouton flottant pour ajouter des évaluations
        view.findViewById(R.id.fab_add_evaluation).setOnClickListener(v -> {
            // Ouvrir le dialog pour ajouter une évaluation
            AddEvaluationDialogFragment dialog = AddEvaluationDialogFragment.newInstance(classId);
            dialog.show(getParentFragmentManager(), "AddEvaluationDialog");
        });

        return view;
    }
}
