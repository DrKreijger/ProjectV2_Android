package com.example.projectv2_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.adapters.EvaluationsListAdapter;
import com.example.projectv2_android.controllers.EvaluationController;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.dialogs.AddEvaluationDialogFragment;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.repositories.ClassRepository;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.services.EvaluationService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class EvaluationsListFragment extends Fragment {

    private static final String ARG_CLASS_ID = "class_id";

    private long classId;
    private RecyclerView recyclerView;
    private EvaluationsListAdapter adapter;
    private EvaluationController evaluationController;

    public static EvaluationsListFragment newInstance(long classId) {
        EvaluationsListFragment fragment = new EvaluationsListFragment();
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

        // Initialisation des repositories
        NoteRepository noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());
        EvaluationRepository evaluationRepository = new EvaluationRepository(
                AppDatabase.getInstance(requireContext()).evaluationDao(),
                noteRepository
        );

        // Initialisation du service et du contrôleur avec les deux repositories
        EvaluationService evaluationService = new EvaluationService(evaluationRepository, noteRepository);
        evaluationController = new EvaluationController(evaluationService);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluations_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_evaluations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new EvaluationsListAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddEvaluation = view.findViewById(R.id.fab_add_evaluation);
        fabAddEvaluation.setOnClickListener(v -> {
            AddEvaluationDialogFragment dialog = AddEvaluationDialogFragment.newInstance(classId);
            dialog.setOnEvaluationAddedListener(this::loadEvaluations);
            dialog.show(getParentFragmentManager(), "AddEvaluationDialog");
        });

        loadClassName(view);
        loadEvaluations();
        return view;
    }

    private void loadEvaluations() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Evaluation> evaluations = evaluationController.getAllEvaluationsForClass(classId);

                requireActivity().runOnUiThread(() -> adapter.setData(evaluations));
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Erreur lors du chargement des évaluations", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadClassName(View view) {
        TextView classNameTextView = view.findViewById(R.id.text_evaluation_list_title);

        if (classNameTextView == null) {
            Toast.makeText(requireContext(), "Erreur : TextView introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Charger le nom de la classe depuis le repository
                ClassRepository classRepository = new ClassRepository(AppDatabase.getInstance(requireContext()).classDao());
                String className = classRepository.getClassNameById(classId);

                if (className == null || className.isEmpty()) {
                    className = "Classe inconnue"; // Valeur par défaut
                }

                // Obtenir la chaîne formatée depuis les ressources
                String displayText = getString(R.string.evaluation_list_title, className);

                requireActivity().runOnUiThread(() -> classNameTextView.setText(displayText));
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    classNameTextView.setText(getString(R.string.evaluation_list_title, "Classe inconnue"));
                    Toast.makeText(requireContext(), "Erreur lors du chargement de la classe", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }




}
