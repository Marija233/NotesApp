package com.example.notesapp.util;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.notesapp.model.Note;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public final class JobSchedulerManager {
    private JobSchedulerManager() {
    }

    public static void startSyncService(Note note, Context context) {
        Gson gson = new Gson();
        String jsonNote = gson.toJson(note);

        Data.Builder data = new Data.Builder();
        data.putString(Constants.INPUT_DATA_NOTIFICATION_DESCRIPTION, jsonNote);

        OneTimeWorkRequest oneTimeWorkRequest;
        oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ReminderService.class)
                .setInputData(data.build())
                .setInitialDelay(calculateTimeToStartTheService(note.reminder), TimeUnit.MILLISECONDS).addTag(String.valueOf(note.id)).build();
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);
    }

    public static void stopSyncService(int syncServiceId, Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(syncServiceId));
    }

    private static long calculateTimeToStartTheService(long time) {
        return time - Calendar.getInstance().getTimeInMillis();
    }

}
