package com.example.notesapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.notesapp.R;
import com.example.notesapp.model.Note;
import com.example.notesapp.model.NotesDatabase;

import java.util.concurrent.Executors;

public class AddNoteActivity extends AppCompatActivity {

    private EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        noteText = findViewById(R.id.note);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.add_note));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveNote() {
        if (!noteText.getText().toString().isEmpty()) {
            Note note = new Note();
            note.noteText = noteText.getText().toString();
            note.createdAt = System.currentTimeMillis();
            Executors.newSingleThreadExecutor().execute(() -> NotesDatabase.getInstance(this).noteDao().insertAll(note));
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            saveNote();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}