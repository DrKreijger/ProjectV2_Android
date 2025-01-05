package com.example.projectv2_android.dialogs;

import android.os.Bundle;
import android.util.Log;
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
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.models.Note;
import com.example.projectv2_android.repositories.NoteRepository;

import java.util.concurrent.Executors;

public class EditNoteDialogFragment extends DialogFragment {

    public interface OnNoteAddedListener {
        void onNoteAdded();
    }

    private static final String TAG = "EditNoteDialogFragment";

    private long studentId;
    private long evaluationId;
    private OnNoteAddedListener listener;
    private NoteRepository noteRepository;

    public static EditNoteDialogFragment newInstance(long studentId, long evaluationId) {
        Log.d("EditNoteDialogFragment", "newInstance - studentId=" + studentId + ", evaluationId=" + evaluationId);
        EditNoteDialogFragment fragment = new EditNoteDialogFragment();
        Bundle args = new Bundle();
        args.putLong("studentId", studentId);
        args.putLong("evaluationId", evaluationId);
        fragment.setArguments(args);
        return fragment;
    }


    public void setOnNoteAddedListener(OnNoteAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_note, container, false);

        EditText inputNote = view.findViewById(R.id.input_student_note);
        Button btnAddNote = view.findViewById(R.id.btn_save_note);

        // Récupération des arguments
        if (getArguments() != null) {
            studentId = getArguments().getLong("studentId", -1);
            evaluationId = getArguments().getLong("evaluationId", -1);
        }

        Log.d(TAG, "onCreateView: studentId=" + studentId + ", evaluationId=" + evaluationId);

        // Validation des IDs
        if (studentId <= 0 || evaluationId <= 0) {
            Log.e(TAG, "Invalid studentId or evaluationId: studentId=" + studentId + ", evaluationId=" + evaluationId);
            Toast.makeText(requireContext(), "IDs invalides. Impossible de sauvegarder la note.", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        // Initialisation du repository
        noteRepository = new NoteRepository(AppDatabase.getInstance(requireContext()).noteDao());

        btnAddNote.setOnClickListener(v -> {
            String noteValueStr = inputNote.getText().toString().trim();

            if (noteValueStr.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez entrer une note", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double noteValue = Double.parseDouble(noteValueStr);

                // Validation de la plage de la note
                if (noteValue < 0 || noteValue > 20) { // Ajuster la limite supérieure si nécessaire
                    Toast.makeText(requireContext(), "La note doit être comprise entre 0 et 20", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Exécuter l'insertion ou la mise à jour dans un thread séparé
                saveNoteInDatabase(noteValue);

            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid note value: " + noteValueStr, e);
                Toast.makeText(requireContext(), "Veuillez entrer une valeur numérique valide", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void saveNoteInDatabase(double noteValue) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Log.d(TAG, "saveNoteInDatabase - studentId=" + studentId + ", evaluationId=" + evaluationId + ", noteValue=" + noteValue);

                Note note = noteRepository.getNoteForStudentEvaluation(studentId, evaluationId);
                if (note == null) {
                    Log.d(TAG, "Creating a new note for studentId=" + studentId + ", evaluationId=" + evaluationId);
                    note = new Note();
                    note.setStudentId(studentId);
                    note.setEvalId(evaluationId);
                }

                note.setNoteValue(noteValue);
                long noteId = noteRepository.insertOrUpdate(note);
                Log.d(TAG, "Note saved with ID: " + noteId);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Note enregistrée avec succès", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onNoteAdded();
                    }
                    dismiss();
                });

            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de l'enregistrement de la note", e);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Erreur lors de l'enregistrement de la note", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
