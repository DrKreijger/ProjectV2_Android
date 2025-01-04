package com.example.projectv2_android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.projectv2_android.R;

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
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_class, null);

        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Ajouter une Classe")
                .setView(view)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    // Logic to add class (e.g., call controller to insert into the database)
                    if (listener != null) {
                        listener.onClassAdded();
                    }
                })
                .setNegativeButton("Annuler", null)
                .create();
    }
}
