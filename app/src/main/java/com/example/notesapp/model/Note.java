package com.example.notesapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note")
public class Note {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "note_text")
    public String noteText;
    @ColumnInfo(name = "created_at")
    public long createdAt;
    public long reminder;

    public String url;
    public String imagePath;
}
