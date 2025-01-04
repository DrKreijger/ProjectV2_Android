package com.example.projectv2_android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.models.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ClassViewHolder> {

    private final List<Class> classList = new ArrayList<>();
    private final OnClassInteractionListener listener;

    public interface OnClassInteractionListener {
        void onClassInteraction(long classId, boolean viewEvaluations);
    }

    public ClassesListAdapter(OnClassInteractionListener listener) {
        this.listener = listener;
    }

    public void setData(List<Class> classes) {
        classList.clear();
        classList.addAll(classes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class cls = classList.get(position);
        holder.bind(cls);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {

        private final TextView textClassName;
        private final Button btnViewEvaluations;
        private final Button btnViewStudents;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textClassName = itemView.findViewById(R.id.text_class_name);
            btnViewEvaluations = itemView.findViewById(R.id.btn_view_evaluations);
            btnViewStudents = itemView.findViewById(R.id.btn_view_students);
        }

        public void bind(Class cls) {
            textClassName.setText(cls.getName());

            btnViewEvaluations.setOnClickListener(v ->
                    listener.onClassInteraction(cls.getId(), true));

            btnViewStudents.setOnClickListener(v ->
                    listener.onClassInteraction(cls.getId(), false));
        }
    }
}
