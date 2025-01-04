package com.example.projectv2_android.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.adapters.StudentEvaluationsAdapter;
import com.example.projectv2_android.controllers.NoteController;
import com.example.projectv2_android.models.Evaluation;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.db.AppDatabase;

import java.util.List;

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
            studentId = getArguments().getLong(ARG_STUDENT_ID);
            classId = getArguments().getLong(ARG_CLASS_ID);
        }

        // Initialisation des DAO et repositories via AppDatabase
        NoteRepository noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());
        evaluationRepository = new EvaluationRepository(
                AppDatabase.getInstance(requireContext()).evaluationDao(),
                noteRepository
        );
        noteController = new NoteController(noteRepository);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        textStudentName = view.findViewById(R.id.text_student_name);
        recyclerView = view.findViewById(R.id.recycler_evaluations);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StudentEvaluationsAdapter(this::onForceAverageClicked);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStudentDetails();
        loadStudentEvaluations();
    }

    private void loadStudentDetails() {
        // TODO: Récupérer les détails de l'étudiant depuis le StudentRepository
        textStudentName.setText("Étudiant #" + studentId); // Remplacez par les données réelles
    }

    private void loadStudentEvaluations() {
        // Récupérer les évaluations pour la classe et les notes pour l'étudiant
        List<Evaluation> evaluations = evaluationRepository.getAllEvaluationsForClass(classId);
        List<Note> notes = noteController.getNotesForStudent(studentId);

        // Associer les évaluations et les notes dans l'adaptateur
        adapter.setData(evaluations, notes);
    }

    private void onForceAverageClicked(long evaluationId) {
        // TODO: Implémentez la logique pour forcer la moyenne (ouvrir un dialog, etc.)
    }
}
