package com.example.projectv2_android.models;

import com.example.projectv2_android.repositories.NoteRepository;

public class LeafEvaluation extends Evaluation {
    private final NoteRepository noteRepository;

    public LeafEvaluation(String name, long classId, Long parentId, Integer pointsMax, NoteRepository noteRepository) {
        super(name, classId, parentId, pointsMax, true); // isLeaf = true
        this.noteRepository = noteRepository;
    }

}
