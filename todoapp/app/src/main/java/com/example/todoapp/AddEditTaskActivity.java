package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.workers.ReminderScheduler;
import com.example.todoapp.models.Task;
import com.example.todoapp.repository.FirebaseRepository;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, dueDateInput, priorityInput;
    private Button saveButton, deleteButton;
    private FirebaseRepository repo;
    private String taskId;
    private long dueDateTs = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        dueDateInput = findViewById(R.id.due_date_input);
        priorityInput = findViewById(R.id.priority_input);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);

        repo = new FirebaseRepository();
        DatabaseReference tasksRef = repo.getTasksRef();

        taskId = getIntent().getStringExtra("TASK_ID");
        if (taskId != null) {
            // режим редактирования
            tasksRef.child(taskId).get().addOnSuccessListener(snap -> {
                Task t = snap.getValue(Task.class);
                if (t != null) {
                    titleInput.setText(t.getTitle());
                    descriptionInput.setText(t.getDescription());
                    dueDateTs = t.getDueDate();
                    dueDateInput.setText(Utils.formatDateTime(dueDateTs));
                    priorityInput.setText(String.valueOf(t.getPriority()));
                    deleteButton.setEnabled(true);
                }
            });
        }

        // клик по полю даты
        dueDateInput.setOnClickListener(v -> {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show();
            pickDateTime();
        });

        saveButton.setOnClickListener(v -> {
            Log.d("AddEditTask", "saveButton clicked");
            saveTask();
        });

        deleteButton.setOnClickListener(v -> {
            if (taskId != null) {
                tasksRef.child(taskId).removeValue().addOnSuccessListener(a -> finish());
            }
        });
    }

    private void pickDateTime() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            c.set(y, m, d);
            new TimePickerDialog(this, (view2, h, min) -> {
                c.set(Calendar.HOUR_OF_DAY, h);
                c.set(Calendar.MINUTE, min);
                dueDateTs = c.getTimeInMillis();
                dueDateInput.setText(Utils.formatDateTime(dueDateTs));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTask() {
        String title = titleInput.getText().toString().trim();
        String desc  = descriptionInput.getText().toString().trim();
        String prioS = priorityInput.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || prioS.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dueDateTs == 0L) {
            Toast.makeText(this, "Пожалуйста, выберите дату выполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        int prio;
        try {
            prio = Integer.parseInt(prioS);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный приоритет", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = repo.getTasksRef();
        if (taskId == null) {
            taskId = ref.push().getKey();
        }

        Task t = new Task(taskId, title, desc, dueDateTs, prio);
        ref.child(taskId).setValue(t)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show();
                    // Запланировать напоминание
                    long delay = dueDateTs - System.currentTimeMillis();
                    if (delay > 0) {
                        ReminderScheduler.scheduleReminder(this, title, delay);
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка сохранения: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("AddEditTask", "save failed", e);
                });
    }
}
