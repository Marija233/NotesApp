package com.example.notesapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.model.Note;
import com.example.notesapp.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnClickListener {

        void onItemClick(int noteId);
    }

    private final List<Note> notes;
    public final OnClickListener listener;

    public NotesAdapter(OnClickListener listener) {
        this.listener = listener;
        notes = new ArrayList<>();
    }

    public void addAll(List<Note> noteList) {
        notes.clear();
        notes.addAll(noteList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewHolder = inflater.inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView noteTextView;
        private final TextView createdAt;
        private final TextView reminder;
        private final RelativeLayout container;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.note);
            createdAt = itemView.findViewById(R.id.createdDate);
            container = itemView.findViewById(R.id.container);
            reminder = itemView.findViewById(R.id.reminder);
        }

        public void bind(Note note) {
            noteTextView.setText(note.noteText);
            createdAt.setText(DateTimeUtil.format(note.createdAt));
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(note.id);
                }
            });

            if (note.reminder == 0) {
                reminder.setVisibility(View.GONE);
            } else {
                reminder.setVisibility(View.VISIBLE);
                reminder.setText(DateTimeUtil.format(note.reminder));
            }
        }
    }
}

