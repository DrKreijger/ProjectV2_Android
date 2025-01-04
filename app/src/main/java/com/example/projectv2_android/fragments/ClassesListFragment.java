package com.example.projectv2_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2_android.R;
import com.example.projectv2_android.adapters.ClassesListAdapter;
import com.example.projectv2_android.controllers.ClassController;
import com.example.projectv2_android.dialogs.AddClassDialogFragment;
import com.example.projectv2_android.models.Class;
import com.example.projectv2_android.repositories.ClassRepository;
import com.example.projectv2_android.db.AppDatabase;

import java.util.List;

public class ClassesListFragment extends Fragment implements ClassesListAdapter.OnClassInteractionListener {

    private RecyclerView recyclerView;
    private ClassesListAdapter adapter;
    private ClassController classController;

    public interface ClassesListNavigator {
        void openStudentsList(long classId);
        void openEvaluationsList(long classId);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_classes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ClassRepository classRepository = new ClassRepository(AppDatabase.getInstance(requireContext()).classDao());
        classController = new ClassController(classRepository);

        adapter = new ClassesListAdapter(this);
        recyclerView.setAdapter(adapter);

        loadClasses();

        view.findViewById(R.id.fab_add_class).setOnClickListener(v -> {
            AddClassDialogFragment dialog = new AddClassDialogFragment();
            dialog.setOnClassAddedListener(this::loadClasses);
            dialog.show(getParentFragmentManager(), "AddClassDialog");
        });

        return view;
    }

    private void loadClasses() {
        List<Class> classes = classController.getAllClasses();
        adapter.setData(classes);
    }

    @Override
    public void onClassInteraction(long classId, boolean viewEvaluations) {
        if (viewEvaluations) {
            // Navigate to EvaluationsListFragment
        } else {
            // Navigate to StudentsListFragment
        }
    }
}
