package com.example.projectv2_android.adapters;

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

    public void setData(List<Student> students) {
        studentList.clear();
        studentList.addAll(students);
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
        Student student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {

        private final TextView textStudentName;
        private final TextView textStudentAverage;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudentName = itemView.findViewById(R.id.text_student_name);
            textStudentAverage = itemView.findViewById(R.id.text_student_average);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Student student = studentList.get(position);
                    listener.onStudentClicked(student.getId());
                }
            });
        }

        public void bind(Student student) {
            textStudentName.setText(student.getFirstName() + " " + student.getName());

            // Calculer la moyenne via le service
            double average = studentService.calculateStudentAverage(student.getId());
            textStudentAverage.setText(String.format("Moyenne: %.2f", average));
        }
    }
}
