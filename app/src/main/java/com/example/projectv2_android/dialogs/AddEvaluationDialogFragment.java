package com.example.projectv2_android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2_android.R;
import com.example.projectv2_android.controllers.EvaluationController;

public class AddEvaluationDialogFragment extends DialogFragment {

    private static final String ARG_CLASS_ID = "classId";

    private long classId;
    private EvaluationController evaluationController;
    private OnEvaluationAddedListener listener;

    public interface OnEvaluationAddedListener {
        void onEvaluationAdded();
    }

    public static AddEvaluationDialogFragment newInstance(long classId) {
        AddEvaluationDialogFragment fragment = new AddEvaluationDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_evaluation, container, false);

        EditText inputName = view.findViewById(R.id.input_evaluation_name);
        EditText inputPointsMax = view.findViewById(R.id.input_evaluation_points_max);
        CheckBox checkBoxSubEvaluations = view.findViewById(R.id.checkbox_has_sub_evaluations);
        Button btnAdd = view.findViewById(R.id.btn_add_evaluation);

        btnAdd.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String pointsMaxStr = inputPointsMax.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pointsMaxStr)) {
                Toast.makeText(getContext(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            int pointsMax = Integer.parseInt(pointsMaxStr);
            boolean hasSubEvaluations = checkBoxSubEvaluations.isChecked();

            evaluationController.createEvaluation(name, classId, pointsMax, hasSubEvaluations, null);

            if (listener != null) {
                listener.onEvaluationAdded();
            }
            dismiss();
        });

        return view;
    }

    public void setOnEvaluationAddedListener(OnEvaluationAddedListener listener) {
        this.listener = listener;
    }
}
