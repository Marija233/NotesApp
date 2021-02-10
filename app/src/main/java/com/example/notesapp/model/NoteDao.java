package com.example.notesapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface NoteDao {

    @Insert
    List<Long> insertAll(Note... notes);

    @Query("SELECT * FROM note WHERE id= :noteId")
    Note getNote(int noteId);

    @Query("SELECT * FROM note")
    List<Note> getAllNotes();

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM note WHERE note_text LIKE :query")
    List<Note> search(String query);

}
