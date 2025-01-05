package com.example.projectv2_android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.adapters.StudentsListAdapter;
import com.example.projectv2_android.controllers.StudentController;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.dialogs.AddStudentDialogFragment;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.repositories.StudentRepository;
import com.example.projectv2_android.services.StudentService;

import java.util.List;
import java.util.concurrent.Executors;

public class StudentsListFragment extends Fragment {

    private static final String TAG = "StudentsListFragment";
    private static final String ARG_CLASS_ID = "class_id";

    private long classId;

    private RecyclerView recyclerView;
    private StudentsListAdapter adapter;
    private StudentController studentController;

    public StudentsListFragment() {
        // Required empty public constructor
    }

    public static StudentsListFragment newInstance(long classId) {
        StudentsListFragment fragment = new StudentsListFragment();
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

        try {
            // Initialisation des repositories
            StudentRepository studentRepository = new StudentRepository(AppDatabase.getInstance(requireContext()).studentDao());
            EvaluationRepository evaluationRepository = new EvaluationRepository(
                    AppDatabase.getInstance(requireContext()).evaluationDao(),
                    new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao())
            );
            NoteRepository noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());

            // Initialisation du service avec tous les repositories nécessaires
            StudentService studentService = new StudentService(studentRepository, evaluationRepository, noteRepository);

            // Initialisation du controller
            studentController = new StudentController(studentService);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing dependencies: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Erreur d'initialisation", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialisation de l'adaptateur
        adapter = new StudentsListAdapter(student -> {
            // Passer l'objet Student entier à la méthode onStudentClicked
            onStudentClicked(student);
        }, studentController.getStudentService());
        recyclerView.setAdapter(adapter);

        // FloatingActionButton pour ajouter un étudiant
        view.findViewById(R.id.fab_add_student).setOnClickListener(v -> {
            AddStudentDialogFragment dialog = AddStudentDialogFragment.newInstance(classId);
            dialog.setOnStudentAddedListener(this::loadStudents);
            dialog.show(getParentFragmentManager(), "AddStudentDialog");
        });

        // Charger les étudiants dès que la vue est créée
        loadStudents();
        return view;
    }

    private void loadStudents() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Chargement des étudiants depuis le contrôleur
                List<Student> students = studentController.getStudentsForClass(classId);
                requireActivity().runOnUiThread(() -> {
                    if (students.isEmpty()) {
                        Toast.makeText(requireContext(), "Aucun étudiant trouvé", Toast.LENGTH_SHORT).show();
                    }
                    adapter.setData(students);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading students: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Erreur lors du chargement des étudiants", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void onStudentClicked(long studentId) {
        // Implémenter la navigation vers le détail de l'étudiant
        if (getActivity() instanceof StudentsListNavigator) {
            ((StudentsListNavigator) getActivity()).openStudentDetail(studentId, classId);
        }
    }

    public interface StudentsListNavigator {
        void openStudentDetail(long studentId, long classId);
    }
}
