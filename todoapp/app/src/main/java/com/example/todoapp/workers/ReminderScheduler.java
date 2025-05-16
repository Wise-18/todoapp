package com.example.todoapp.workers;

import android.content.Context;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {
    public static void scheduleReminder(Context ctx,String title,long delay){
        Data d=new Data.Builder().putString("TASK_TITLE",title).build();
        OneTimeWorkRequest r=new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay,TimeUnit.MILLISECONDS)
                .setInputData(d).build();
        WorkManager.getInstance(ctx).enqueue(r);
    }
}
