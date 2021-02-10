package com.example.notesapp.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.notesapp.model.Note;
import com.example.notesapp.model.NotesDatabase;
import com.google.gson.Gson;
import java.util.concurrent.Executors;

public class ReminderService extends Worker {


    public ReminderService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {
        Gson gson = new Gson();
        String noteJson = getInputData().getString(Constants.INPUT_DATA_NOTIFICATION_DESCRIPTION);
        Note note = gson.fromJson(noteJson, Note.class);
        NotificationUtil.sendNotification(getApplicationContext(), note.id, note.noteText);
        note.reminder = 0;
        Executors.newSingleThreadExecutor().execute(() -> {
            NotesDatabase.getInstance(getApplicationContext()).noteDao().update(note);
        });

        return ListenableWorker.Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }
}
