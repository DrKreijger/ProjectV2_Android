package com.example.projectv2_android.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.models.Student;
import com.example.projectv2_android.services.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.StudentViewHolder> {

    private final List<Student> studentList = new ArrayList<>();
    private final OnStudentClickListener listener;
    private final StudentService studentService;

    public interface OnStudentClickListener {
        void onStudentClicked(long studentId);
    }

    public StudentsListAdapter(OnStudentClickListener listener, StudentService studentService) {
        this.listener = listener;
        this.studentService = studentService;
    }

    /**
     * Met à jour la liste des étudiants.
     */
    public void setData(List<Student> students) {
        studentList.clear();
        if (students != null) {
            studentList.addAll(students);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        if (position >= 0 && position < studentList.size()) {
            Student student = studentList.get(position);
            holder.bind(student);
        } else {
            // Log en cas d'incohérence
            Log.w("StudentsListAdapter", "Index " + position + " out of bounds for studentList.");
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {

        private final TextView textStudentName;
        private final TextView textStudentAverage;
        private final TextView textStudentMatricule;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudentName = itemView.findViewById(R.id.text_student_name);
            textStudentAverage = itemView.findViewById(R.id.text_student_average);
            textStudentMatricule = itemView.findViewById(R.id.text_student_matricule);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < studentList.size()) {
                    Student student = studentList.get(position);
                    listener.onStudentClicked(student.getId());
                }
            });
        }

        public void bind(Student student) {
            // Affiche le nom complet de l'étudiant
            textStudentName.setText(String.format(Locale.getDefault(), "%s %s", student.getFirstName(), student.getName()));

            // Affiche le matricule de l'étudiant
            textStudentMatricule.setText(String.format(Locale.getDefault(), "Matricule : %s", student.getMatricule()));

            // Calcul et affichage de la moyenne pondérée
            studentService.calculateStudentAverage(student.getId(), new StudentService.Callback<Double>() {
                @Override
                public void onSuccess(Double average) {
                    // Affiche la moyenne arrondie sur 20
                    double roundedAverage = roundToNearestHalf(average);
                    textStudentAverage.post(() -> textStudentAverage.setText(
                            String.format(Locale.getDefault(), "%.1f / 20", roundedAverage)
                    ));
                }

                @Override
                public void onError(Exception e) {
                    // Affiche une erreur en cas de problème
                    textStudentAverage.post(() -> textStudentAverage.setText("Erreur"));
                }
            });
        }

        private double roundToNearestHalf(double value) {
            return Math.round(value * 2) / 2.0; // Arrondit à 0.5 près
        }
    }
}
