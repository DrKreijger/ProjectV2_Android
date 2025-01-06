package com.example.projectv2_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectv2_android.R;
import com.example.projectv2_android.adapters.StudentEvaluationsAdapter;
import com.example.projectv2_android.controllers.NoteController;
import com.example.projectv2_android.dialogs.EditNoteDialogFragment;
import com.example.projectv2_android.dialogs.ForceNoteDialogFragment;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.services.EvaluationService;

import java.util.List;
import java.util.concurrent.Executors;

public class StudentDetailFragment extends Fragment {

    private static final String ARG_STUDENT_ID = "student_id";
    private static final String ARG_CLASS_ID = "class_id";

    private long studentId;
    private long classId;

    private TextView textStudentName;
    private RecyclerView recyclerView;

    private StudentEvaluationsAdapter adapter;
    private NoteController noteController;
    private EvaluationRepository evaluationRepository;
    private EvaluationService evaluationService;

    public StudentDetailFragment() {
        // Required empty public constructor
    }

    public static StudentDetailFragment newInstance(long studentId, long classId) {
        StudentDetailFragment fragment = new StudentDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        args.putLong(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID, -1);
            classId = getArguments().getLong(ARG_CLASS_ID, -1);
        }

        // Initialiser les repositories et les services
        NoteRepository noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());
        evaluationRepository = new EvaluationRepository(
                AppDatabase.getInstance(requireContext()).evaluationDao(),
                noteRepository
        );
        noteController = new NoteController(noteRepository);
        evaluationService = new EvaluationService(evaluationRepository, noteRepository);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        textStudentName = view.findViewById(R.id.text_student_name);
        recyclerView = view.findViewById(R.id.recycler_evaluations);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentEvaluationsAdapter(studentId,this::onEvaluationClicked, this::onForceAverageClicked, evaluationService);
        recyclerView.setAdapter(adapter);

        // Ajout du clic pour modifier ou ajouter une note
        adapter.setOnNoteClickListener(this::onEvaluationClicked);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStudentDetails();
        loadStudentEvaluations();
    }

    private void loadStudentDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Simuler la récupération des détails de l'étudiant
                String studentName = "Étudiant #" + studentId; // Remplacez par une requête réelle

                updateUI(() -> textStudentName.setText(studentName));
            } catch (Exception e) {
                showToast("Erreur lors du chargement des détails de l'étudiant");
            }
        });
    }

    private void loadStudentEvaluations() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Evaluation> evaluations = evaluationRepository.getAllEvaluationsForClass(classId);
                List<Note> notes = noteController.getNotesForStudent(studentId);

                requireActivity().runOnUiThread(() -> {
                    if (evaluations.isEmpty()) {
                        showToast("Aucune évaluation trouvée");
                    } else {
                        adapter.setData(evaluations, notes);
                    }
                });
            } catch (Exception e) {
                Log.e("StudentDetailFragment", "Erreur lors du chargement des évaluations ou des notes", e);
                showToast("Erreur : données incohérentes pour les évaluations et les notes");
            }
        });
    }

    private void onEvaluationClicked(long evaluationId) {
        // Ajout d'un log pour vérifier les IDs
        Log.d("StudentDetailFragment", "onEvaluationClicked - studentId: " + studentId + ", evaluationId: " + evaluationId);

        // Vérifiez si les IDs sont valides
        if (studentId <= 0) {
            Log.e("StudentDetailFragment", "Invalid studentId: " + studentId);
            showToast("Erreur : ID de l'étudiant invalide.");
            return;
        }

        if (evaluationId <= 0) {
            Log.e("StudentDetailFragment", "Invalid evaluationId: " + evaluationId);
            showToast("Erreur : ID de l'évaluation invalide.");
            return;
        }

        // Ouvre le dialog pour éditer ou ajouter une note
        EditNoteDialogFragment dialog = EditNoteDialogFragment.newInstance(studentId, evaluationId);
        dialog.setOnNoteAddedListener(this::loadStudentEvaluations);
        dialog.show(getParentFragmentManager(), "EditNoteDialog");
    }

    private void onForceAverageClicked(long evaluationId) {
        // Ouvre le dialog pour forcer une moyenne
        ForceNoteDialogFragment dialog = ForceNoteDialogFragment.newInstance(evaluationId);
        dialog.setOnForceNoteListener(forcedNote -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    noteController.forceNoteForEvaluation(studentId, evaluationId, forcedNote);
                    updateUI(this::loadStudentEvaluations);
                } catch (Exception e) {
                    showToast("Erreur lors de la mise à jour de la note forcée");
                }
            });
        });
        dialog.show(getParentFragmentManager(), "ForceAverageDialog");
    }

    private void showToast(String message) {
        updateUI(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void updateUI(Runnable action) {
        requireActivity().runOnUiThread(action);
    }
}
