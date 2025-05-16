package com.example.todoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.R;
import com.example.todoapp.models.Task;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {
    public interface OnTaskClick { void onClick(Task t); }
    private final List<Task> list; private final OnTaskClick click;
    public TaskAdapter(List<Task> l, OnTaskClick c){ list=l; click=c; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v){
        View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_task,p,false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        Task t = list.get(pos);
        h.title.setText(t.getTitle());
        h.desc.setText(t.getDescription());
        h.due.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(t.getDueDate()));
        h.prio.setText("Priority: "+t.getPriority());
        h.itemView.setOnClickListener(v->click.onClick(t));
    }
    @Override public int getItemCount(){ return list.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView title,desc,due,prio;
        VH(@NonNull View v){ super(v);
            title=v.findViewById(R.id.task_title);
            desc=v.findViewById(R.id.task_description);
            due=v.findViewById(R.id.task_due_date);
            prio=v.findViewById(R.id.task_priority);
        }
    }
}
