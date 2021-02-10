package com.example.notesapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NotesDatabase extends RoomDatabase {

    private static final String DB_NAME = "notes_db";
    private static NotesDatabase instance;

    public static synchronized NotesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NotesDatabase.class,
                    DB_NAME)
                    .build();
        }

        return instance;
    }

    public abstract NoteDao noteDao();

}
