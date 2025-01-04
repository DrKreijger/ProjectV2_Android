package com.example.projectv2_android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2_android.R;
import com.example.projectv2_android.controllers.StudentController;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.repositories.EvaluationRepository;
import com.example.projectv2_android.repositories.NoteRepository;
import com.example.projectv2_android.repositories.StudentRepository;
import com.example.projectv2_android.services.StudentService;

public class AddStudentDialogFragment extends DialogFragment {

    public interface OnStudentAddedListener {
        void onStudentAdded();
    }

    private OnStudentAddedListener listener;
    private long classId;
    private StudentController studentController;

    public static AddStudentDialogFragment newInstance(long classId) {
        AddStudentDialogFragment fragment = new AddStudentDialogFragment();
        Bundle args = new Bundle();
        args.putLong("class_id", classId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnStudentAddedListener(OnStudentAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            classId = getArguments().getLong("class_id");
        }

        // Initialisation correcte des repositories et services
        StudentRepository studentRepository = new StudentRepository(AppDatabase.getInstance(requireContext()).studentDao());
        NoteRepository noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());
        EvaluationRepository evaluationRepository = new EvaluationRepository(
                AppDatabase.getInstance(requireContext()).evaluationDao(),
                noteRepository
        );

        StudentService studentService = new StudentService(
                studentRepository,
                evaluationRepository,
                noteRepository
        );

        studentController = new StudentController(studentService);

        EditText inputFirstName = view.findViewById(R.id.input_student_first_name);
        EditText inputLastName = view.findViewById(R.id.input_student_last_name);
        EditText inputMatricule = view.findViewById(R.id.input_student_matricule);
        Button btnAddStudent = view.findViewById(R.id.btn_add_student);

        btnAddStudent.setOnClickListener(v -> {
            String firstName = inputFirstName.getText().toString().trim();
            String lastName = inputLastName.getText().toString().trim();
            String matricule = inputMatricule.getText().toString().trim();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(matricule)) {
                Toast.makeText(requireContext(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long studentId = studentController.createStudent(firstName, lastName, matricule, classId);
                Toast.makeText(requireContext(), R.string.student_added_successfully, Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onStudentAdded();
                }
                dismiss();
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), R.string.error_adding_student, Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onStudentAdded();
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.add_student);
        return dialog;
    }
}
