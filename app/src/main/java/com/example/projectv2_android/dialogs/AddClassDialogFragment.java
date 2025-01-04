package com.example.projectv2_android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2_android.R;
import com.example.projectv2_android.controllers.ClassController;
import com.example.projectv2_android.db.AppDatabase;
import com.example.projectv2_android.repositories.ClassRepository;
import com.example.projectv2_android.services.ClassService;

import java.util.concurrent.Executors;

public class AddClassDialogFragment extends DialogFragment {

    public interface OnClassAddedListener {
        void onClassAdded();
    }

    private OnClassAddedListener listener;

    public void setOnClassAddedListener(OnClassAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the view for the dialog
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_class, null);
        EditText inputClassName = view.findViewById(R.id.input_class_name);

        // Initialize the repository, service, and controller
        ClassRepository classRepository = new ClassRepository(AppDatabase.getInstance(requireContext()).classDao());
        ClassService classService = new ClassService(classRepository); // Ajout de la couche service
        ClassController classController = new ClassController(classService); // Injection de service dans le contrôleur

        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Ajouter une Classe")
                .setView(view)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String className = inputClassName.getText().toString().trim();

                    if (TextUtils.isEmpty(className)) {
                        Toast.makeText(requireContext(), "Le nom de la classe ne peut pas être vide", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Insert the class using a background thread
                    Executors.newSingleThreadExecutor().execute(() -> {
                        long classId = classController.addClass(className);
                        requireActivity().runOnUiThread(() -> {
                            if (classId > 0) {
                                Toast.makeText(requireContext(), "Classe ajoutée avec succès", Toast.LENGTH_SHORT).show();
                                if (listener != null) {
                                    listener.onClassAdded();
                                }
                            } else {
                                Toast.makeText(requireContext(), "Erreur lors de l'ajout de la classe", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("Annuler", null)
                .create();
    }
}

