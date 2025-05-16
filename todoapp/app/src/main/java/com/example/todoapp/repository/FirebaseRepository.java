package com.example.todoapp.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRepository {
    private final DatabaseReference tasksRef;
    public FirebaseRepository() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://todoapp-409e0-default-rtdb.europe-west1.firebasedatabase.app");
        tasksRef = db.getReference("tasks");
    }
    public DatabaseReference getTasksRef() { return tasksRef; }
}
