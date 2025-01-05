package com.example.projectv2_android.dialogs;

import android.os.Bundle;
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

    private long studentId;
    private long evaluationId;
    private OnNoteAddedListener listener;
    private NoteRepository noteRepository;

    public static EditNoteDialogFragment newInstance(long studentId, long evaluationId) {
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

        if (getArguments() != null) {
            studentId = getArguments().getLong("studentId");
            evaluationId = getArguments().getLong("evaluationId");
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

                // Validation de la plage de la note (par exemple, entre 0 et 20)
                if (noteValue < 0 || noteValue > 20) { // Ajustez la limite supérieure si nécessaire
                    Toast.makeText(requireContext(), "La note doit être comprise entre 0 et 20", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Créer une note et exécuter l'insertion ou la mise à jour dans un thread séparé
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        Note note = new Note();
                        note.setStudentId(studentId);
                        note.setEvalId(evaluationId);
                        note.setNoteValue(noteValue);

                        noteRepository.insertOrUpdate(note);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Note ajoutée avec succès", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onNoteAdded();
                            }
                            dismiss();
                        });
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Erreur lors de l'ajout de la note", Toast.LENGTH_SHORT).show();
                        });
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Veuillez entrer une valeur numérique valide", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
