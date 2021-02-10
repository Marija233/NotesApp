package com.example.notesapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.notesapp.R;
import com.example.notesapp.model.Note;
import com.example.notesapp.model.NotesDatabase;
import com.example.notesapp.util.Constants;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnClickListener {

    private NotesAdapter adapter;
    private SearchView searchView;
    private TextView numberOfNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUi();
    }

    private void setupUi() {
        numberOfNotes = findViewById(R.id.numberOfNotes);
        adapter = new NotesAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        searchView= findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.isEmpty())
                {
                    getNotes();
                }
                else
                {
                    searchNote(newText);
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNotes();
    }

    private void searchNote(String query)
    {
        String queryParam = "%"+query+"%";
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Note> noteList = NotesDatabase.getInstance(this).noteDao().search(queryParam);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayNotes(noteList);
                }
            });

        });

    }

    private void getNotes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Note> noteList = NotesDatabase.getInstance(this).noteDao().getAllNotes();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayNotes(noteList);
                }
            });

        });
    }

    private void displayNotes(List<Note> noteList) {
        adapter.addAll(noteList);
        numberOfNotes.setText(getString(R.string.number_of_notes, noteList.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addNote) {
            Intent intent = new Intent(this, AddNoteActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int noteId) {
        Intent intent = new Intent(this, NoteDetailsActivity.class);
        intent.putExtra(Constants.NOTE_ID, noteId);
        startActivity(intent);
    }
}