package com.example.projectv2_android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2_android.R;

public class ForceNoteDialogFragment extends DialogFragment {

    public interface OnForceNoteListener {
        void onForceNoteConfirmed(double forcedNote);
    }

    private static final String ARG_EVALUATION_ID = "evaluation_id";
    private static final String ARG_MAX_POINTS = "max_points";

    private OnForceNoteListener listener;
    private long evaluationId;
    private double maxPoints;

    public static ForceNoteDialogFragment newInstance(long evaluationId, double maxPoints) {
        ForceNoteDialogFragment fragment = new ForceNoteDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EVALUATION_ID, evaluationId);
        args.putDouble(ARG_MAX_POINTS, maxPoints);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnForceNoteListener(OnForceNoteListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            evaluationId = getArguments().getLong(ARG_EVALUATION_ID);
            maxPoints = getArguments().getDouble(ARG_MAX_POINTS);
        }

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_force_average, null);
        EditText inputForceAverage = view.findViewById(R.id.input_force_average);
        Button btnForceAverage = view.findViewById(R.id.btn_force_average);

        btnForceAverage.setOnClickListener(v -> {
            String input = inputForceAverage.getText().toString().trim();

            if (TextUtils.isEmpty(input)) {
                Toast.makeText(requireContext(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double forcedNote = Double.parseDouble(input);

                if (forcedNote < 0 || forcedNote > maxPoints) {
                    Toast.makeText(requireContext(), String.format(
                            getString(R.string.error_invalid_note_range), 0, (int) maxPoints
                    ), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (listener != null) {
                    listener.onForceNoteConfirmed(forcedNote);
                }
                dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), R.string.error_invalid_number, Toast.LENGTH_SHORT).show();
            }
        });

        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.force_note_title))
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Vous pouvez ajouter une action ici si nécessaire
                })
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Optionnel : Action lorsqu'on ferme le dialogue sans valider
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Optionnel : Action lorsqu'on ferme le dialogue en annulant
    }
}
