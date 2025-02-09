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
import com.example.projectv2_android.adapters.StudentEvaluationsAdapter;
import com.example.projectv2_android.controllers.NoteController;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.dialogs.EditNoteDialogFragment;
import com.example.projectv2_android.dialogs.ForceNoteDialogFragment;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.repositories.StudentRepository;
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
        adapter = new StudentEvaluationsAdapter(
                studentId,
                this::onEvaluationClicked,
                this::onForceAverageClicked,
                evaluationService
        );
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
                // Récupérer les informations de l'étudiant depuis la base de données
                StudentRepository studentRepository = new StudentRepository(AppDatabase.getInstance(requireContext()).studentDao());
                Student student = studentRepository.findById(studentId);

                if (student != null) {
                    // Formater le titre avec le prénom et nom de l'étudiant
                    String formattedTitle = getString(R.string.student_detail_title, student.getFirstName() + " " + student.getName());

                    // Mettre à jour l'interface utilisateur
                    updateUI(() -> textStudentName.setText(formattedTitle));
                } else {
                    // Si l'étudiant n'est pas trouvé
                    updateUI(() -> {
                        textStudentName.setText(getString(R.string.student_detail_title, "Inconnu"));
                        showToast("Étudiant introuvable");
                    });
                }
            } catch (Exception e) {
                updateUI(() -> {
                    textStudentName.setText(getString(R.string.student_detail_title, "Inconnu"));
                    showToast("Erreur lors du chargement des détails de l'étudiant");
                });
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
                showToast("Erreur : données incohérentes pour les évaluations et les notes");
            }
        });
    }

    private void onEvaluationClicked(long evaluationId) {

        if (studentId <= 0 || evaluationId <= 0) {
            showToast("Erreur : ID invalide.");
            return;
        }

        // Désactive le clic direct sur les parents
        Executors.newSingleThreadExecutor().execute(() -> {
            Evaluation evaluation = evaluationRepository.findById(evaluationId);
            if (evaluation != null && !evaluation.isLeaf()) {
                updateUI(() -> showToast("Veuillez utiliser le bouton pour forcer la moyenne."));
            } else if (evaluation != null) {
                // Si c'est une feuille, ouvrir le dialog sur le thread principal
                updateUI(() -> {
                    EditNoteDialogFragment dialog = EditNoteDialogFragment.newInstance(studentId, evaluationId);
                    dialog.setOnNoteAddedListener(this::loadStudentEvaluations);
                    dialog.show(getParentFragmentManager(), "EditNoteDialog");
                });
            } else {
                updateUI(() -> showToast("Erreur : Évaluation non trouvée."));
            }
        });
    }


    private void onForceAverageClicked(long evaluationId, double maxPoints) {
        ForceNoteDialogFragment dialog = ForceNoteDialogFragment.newInstance(evaluationId, maxPoints);
        dialog.setOnForceNoteListener(forcedNote -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    noteController.forceNoteForEvaluation(studentId, evaluationId, forcedNote);
                    updateUI(this::loadStudentEvaluations);
                } catch (Exception e) {
                    showToast("Erreur : impossible de forcer la note.");
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
