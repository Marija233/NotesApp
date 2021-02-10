package com.example.notesapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.model.Note;
import com.example.notesapp.model.NotesDatabase;
import com.example.notesapp.util.Constants;
import com.example.notesapp.util.DateTimeUtil;
import com.example.notesapp.util.JobSchedulerManager;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;

public class NoteDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int REQUEST_CODE_STORAGE_PERMISSION =1 ;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private TextView reminder;
    private EditText noteText;
    private Note note;
    private Calendar selectedDate;
    private EditText url;
    private TextView urlText;
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);
        setupUi();
        getNote();
    }

    private void setupUi() {
        reminder = findViewById(R.id.reminder);
        noteText = findViewById(R.id.note);
        url = findViewById(R.id.url);
        urlText = findViewById(R.id.textview);
        image = findViewById(R.id.image_view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.note_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    public void OnClickListener(View view)
    {
        String url1= urlText.getText().toString();
        if(!url1.isEmpty())
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url1));
            startActivity(i);
        }

    }

    private void getNote() {
        int noteId = getIntent().getIntExtra(Constants.NOTE_ID, 0);
        Executors.newSingleThreadExecutor().execute(() -> {
            note = NotesDatabase.getInstance(this).noteDao().getNote(noteId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (note != null) {
                        noteText.setText(note.noteText);
                        urlText.setText(note.url);
                        if(note.imagePath!=null && !note.imagePath.isEmpty())
                        {
                            image.setImageBitmap(BitmapFactory.decodeFile(note.imagePath));
                        }
                        showHideReminder();
                        invalidateOptionsMenu();

                    }
                }
            });
        });
    }

    private void showHideReminder() {
        if (note.reminder != 0) {
            reminder.setText(DateTimeUtil.format(note.reminder));
            reminder.setVisibility(View.VISIBLE);
            JobSchedulerManager.startSyncService(note, getApplication().getApplicationContext());
        } else {
            reminder.setVisibility(View.GONE);
            JobSchedulerManager.stopSyncService(note.id, getApplication().getApplicationContext());
        }
    }






    private void updateNote() {
        Executors.newSingleThreadExecutor().execute(() -> {
            NotesDatabase.getInstance(NoteDetailsActivity.this).noteDao().update(note);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHideReminder();
                    Toast.makeText(NoteDetailsActivity.this, "Note was successfully updated", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                }
            });

        });
    }

    private void deleteNote() {
        Executors.newSingleThreadExecutor().execute(() -> {
            NotesDatabase.getInstance(NoteDetailsActivity.this).noteDao().delete(note);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JobSchedulerManager.stopSyncService(note.id, getApplication().getApplicationContext());
                    Toast.makeText(NoteDetailsActivity.this, "Note was successfully deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_note_menu, menu);
        if (note != null) {
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);

            if (note.reminder == 0) {
                menu.findItem(R.id.action_add_reminder).setVisible(true);
                menu.findItem(R.id.action_remove_reminder).setVisible(false);
            } else {
                menu.findItem(R.id.action_add_reminder).setVisible(false);
                menu.findItem(R.id.action_remove_reminder).setVisible(true);
            }

            if (noteText.isEnabled()) {
                menu.findItem(R.id.action_done).setVisible(true);
                menu.findItem(R.id.action_edit).setVisible(false);
            } else {
                menu.findItem(R.id.action_done).setVisible(false);
                menu.findItem(R.id.action_edit).setVisible(true);
            }

            if(note.imagePath!=null && !note.imagePath.isEmpty()) {
                menu.findItem(R.id.action_add_image).setVisible(false);
                menu.findItem(R.id.action_remove_image).setVisible(true);
                displayImage(Uri.parse(note.imagePath));

            }
            else
            {
                menu.findItem(R.id.action_add_image).setVisible(true);
                menu.findItem(R.id.action_remove_image).setVisible(false);
                image.setVisibility(View.GONE);


            }



        }
        return super.onCreateOptionsMenu(menu);


    }
private void deleteImage()
{
    note.imagePath = null;
    updateNote();

}

private void selectImage()
{
Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
 if(intent.resolveActivity(getPackageManager())!= null){
     startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
 }
}



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            noteText.setEnabled(true);
            url.setVisibility(View.VISIBLE);
            urlText.setVisibility(View.INVISIBLE);
            String note = noteText.getText().toString();
            //Set the cursor in the edit text at the end of the entered text
            noteText.setSelection(note.length());
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.action_done) {
            noteText.setEnabled(false);
            note.noteText = noteText.getText().toString();
            note.url = url.getText().toString();
            urlText.setText(note.url);
            urlText.setVisibility(View.VISIBLE);
            url.setVisibility(View.INVISIBLE);
            updateNote();
        } else if (item.getItemId() == R.id.action_delete) {
            deleteNote();
        } else if (item.getItemId() == R.id.action_add_reminder) {
            displayDateTimePicker();
        } else if (item.getItemId() == R.id.action_remove_reminder) {
            note.reminder = 0;
            updateNote();
        }
        else if(item.getItemId()== R.id.action_remove_image){
           deleteImage();
        }
        else if(item.getItemId()== R.id.action_add_image)
        {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(
                        NoteDetailsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION

                );
            }
            else{
                selectImage();
            }
        }

        else if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            }
            else
            {
                Toast.makeText(this,"Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){

                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    note.imagePath = getPathFromUri(selectedImageUri);
                    updateNote();
                }

            }
        }
    }

    private void displayImage (Uri uri)
    {

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);


        } catch  (Exception exception){
            Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }


    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null,null,null,null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void displayDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.Theme_AppCompat_Light_Dialog,
                this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void displayTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.Theme_AppCompat_Light_Dialog, this, 0, 0, true);
        timePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        selectedDate = calendar;
        displayTimePicker();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDate.set(Calendar.MINUTE, minute);
        if (selectedDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            Toast.makeText(this, "Time for the reminder can't be in the past", Toast.LENGTH_SHORT).show();
        } else {
            note.reminder = selectedDate.getTimeInMillis();
            updateNote();
        }
    }
}