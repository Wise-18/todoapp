package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.adapters.TaskAdapter;
import com.example.todoapp.models.Task;
import com.example.todoapp.repository.FirebaseRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // убедитесь, что здесь не опечатка!

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, this::openEditTask);
        recyclerView.setAdapter(adapter);

        // Инициализация FAB
        FloatingActionButton addTaskButton = findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEditTaskActivity.class));
        });

        // Загрузка задач
        repository = new FirebaseRepository();
        loadTasks();
    }

    private void loadTasks() {
        repository.getTasksRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Task t = ds.getValue(Task.class);
                    if (t != null) taskList.add(t);
                }
                // Сортировка
                Collections.sort(taskList, Comparator.comparingLong(Task::getDueDate)
                        .thenComparingInt(Task::getPriority));
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void openEditTask(Task task) {
        Intent i = new Intent(this, AddEditTaskActivity.class);
        i.putExtra("TASK_ID", task.getId());
        startActivity(i);
    }
}
