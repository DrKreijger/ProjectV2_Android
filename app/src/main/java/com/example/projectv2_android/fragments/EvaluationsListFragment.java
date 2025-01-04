package com.example.projectv2_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.services.EvaluationService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

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

        // Initialisation du service et du contrôleur
        EvaluationRepository evaluationRepository = new EvaluationRepository(
                AppDatabase.getInstance(requireContext()).evaluationDao(),
                new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao())
        );
        EvaluationService evaluationService = new EvaluationService(evaluationRepository);
        evaluationController = new EvaluationController(evaluationService);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluations_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_evaluations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

//        adapter = new EvaluationsListAdapter(this::onEvaluationClicked);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddEvaluation = view.findViewById(R.id.fab_add_evaluation);
        fabAddEvaluation.setOnClickListener(v -> {
            AddEvaluationDialogFragment dialog = AddEvaluationDialogFragment.newInstance(classId);
            dialog.setOnEvaluationAddedListener(this::loadEvaluations);
            dialog.show(getParentFragmentManager(), "AddEvaluationDialog");
        });

        FloatingActionButton fabBack = view.findViewById(R.id.fab_back);
        fabBack.setOnClickListener(v -> showParentEvaluations());

        loadEvaluations();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadEvaluations();
    }

    private void loadEvaluations() {
        List<Evaluation> evaluations = evaluationController.getAllEvaluationsForClass(classId);
        adapter.setData(evaluations);
    }

//    private void onEvaluationClicked(Evaluation evaluation) {
//        if (evaluation.isLeaf()) {
//            // Naviguer vers le détail de l'évaluation feuille
//            if (getActivity() instanceof EvaluationsListNavigator) {
//                ((EvaluationsListNavigator) getActivity()).openEvaluationDetail(evaluation.getId());
//            }
//        } else {
//            // Charger et afficher les sous-évaluations pour l'évaluation parent
//            List<Evaluation> subEvaluations = evaluationController.getChildEvaluations(evaluation.getId());
//            adapter.setData(subEvaluations);
//
//            // Afficher le bouton "Retour" pour revenir aux évaluations parent
//            FloatingActionButton fabBack = getView().findViewById(R.id.fab_back);
//            if (fabBack != null) {
//                fabBack.setVisibility(View.VISIBLE);
//            }
//        }
//    }


    private void showParentEvaluations() {
        List<Evaluation> evaluations = evaluationController.getAllEvaluationsForClass(classId);
        adapter.setData(evaluations);

        // Cacher le bouton "Retour"
        FloatingActionButton fabBack = getView().findViewById(R.id.fab_back);
        if (fabBack != null) {
            fabBack.setVisibility(View.GONE);
        }
    }

}
