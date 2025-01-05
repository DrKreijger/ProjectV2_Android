package com.example.projectv2_android.dialogs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2_android.R;
import com.example.projectv2_android.controllers.EvaluationController;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.models.LeafEvaluation;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.services.EvaluationService;
import com.example.projectv2_android.models.Evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddEvaluationDialogFragment extends DialogFragment {

    private static final String ARG_CLASS_ID = "classId";

    private long classId;
    private EvaluationController evaluationController;
    private OnEvaluationAddedListener listener;

    public interface OnEvaluationAddedListener {
        void onEvaluationAdded();
    }

    public static AddEvaluationDialogFragment newInstance(long classId) {
        AddEvaluationDialogFragment fragment = new AddEvaluationDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnEvaluationAddedListener(OnEvaluationAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getLong(ARG_CLASS_ID);
        }

        // Initialisation du contrôleur
        NoteRepository noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());
        EvaluationRepository evaluationRepository = new EvaluationRepository(
                AppDatabase.getInstance(requireContext()).evaluationDao(),
                noteRepository
        );
        EvaluationService evaluationService = new EvaluationService(evaluationRepository, noteRepository); // Passez ici les deux repositories
        evaluationController = new EvaluationController(evaluationService);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_evaluation, container, false);

        EditText inputName = view.findViewById(R.id.input_evaluation_name);
        EditText inputPointsMax = view.findViewById(R.id.input_evaluation_points_max);
        CheckBox checkBoxSubEvaluations = view.findViewById(R.id.checkbox_has_sub_evaluations);
        LinearLayout subEvaluationsContainer = view.findViewById(R.id.sub_evaluations_container);
        Button btnAddSubEvaluation = view.findViewById(R.id.btn_add_sub_evaluation);
        Button btnAdd = view.findViewById(R.id.btn_add_evaluation);

        // Gestion de l'affichage des sous-évaluations
        checkBoxSubEvaluations.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subEvaluationsContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Ajout dynamique de sous-évaluations
        btnAddSubEvaluation.setOnClickListener(v -> {
            View subEvaluationView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_sub_evaluation, subEvaluationsContainer, false);
            subEvaluationsContainer.addView(subEvaluationView);
        });

        btnAdd.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String pointsMaxStr = inputPointsMax.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            int pointsMax = 20; // Par défaut 20 points
            try {
                if (!TextUtils.isEmpty(pointsMaxStr)) {
                    pointsMax = Integer.parseInt(pointsMaxStr);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Points maximum invalides", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean hasSubEvaluations = checkBoxSubEvaluations.isChecked();
            List<LeafEvaluation> subEvaluations = new ArrayList<>();

            // Si des sous-évaluations sont définies, les valider et les récupérer
            if (hasSubEvaluations) {
                for (int i = 0; i < subEvaluationsContainer.getChildCount(); i++) {
                    View subEvaluationView = subEvaluationsContainer.getChildAt(i);
                    EditText inputSubName = subEvaluationView.findViewById(R.id.input_sub_evaluation_name);
                    EditText inputSubPointsMax = subEvaluationView.findViewById(R.id.input_sub_evaluation_points_max);

                    String subName = inputSubName.getText().toString().trim();
                    String subPointsMaxStr = inputSubPointsMax.getText().toString().trim();

                    if (TextUtils.isEmpty(subName) || TextUtils.isEmpty(subPointsMaxStr)) {
                        Toast.makeText(getContext(), "Veuillez remplir toutes les sous-évaluations", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int subPointsMax;
                    try {
                        subPointsMax = Integer.parseInt(subPointsMaxStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Points maximum invalides pour une sous-évaluation", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    subEvaluations.add(new LeafEvaluation(
                            subName,
                            classId,
                            null,
                            subPointsMax,
                            new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao())
                    ));
                }
            }

            // Exécuter l'ajout dans un thread en arrière-plan
            int finalPointsMax = pointsMax;
            Executors.newSingleThreadExecutor().execute(() -> {
                long evaluationId;
                if (hasSubEvaluations) {
                    // Convertir la liste de LeafEvaluation en liste générique Evaluation
                    List<Evaluation> subEvaluationsAsEvaluations = new ArrayList<>(subEvaluations);

                    // Création d'une ParentEvaluation
                    evaluationId = evaluationController.createParentEvaluation(name, classId, finalPointsMax, subEvaluationsAsEvaluations, null);
                } else {
                    // Création d'une LeafEvaluation
                    evaluationId = evaluationController.createLeafEvaluation(name, classId, null, finalPointsMax);
                }

                requireActivity().runOnUiThread(() -> {
                    if (evaluationId > 0) {
                        Toast.makeText(getContext(), "Évaluation ajoutée avec succès", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onEvaluationAdded();
                        }
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Erreur lors de l'ajout de l'évaluation", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        return view;
    }

}
