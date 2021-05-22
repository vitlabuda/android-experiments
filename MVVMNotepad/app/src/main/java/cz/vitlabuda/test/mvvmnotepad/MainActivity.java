package cz.vitlabuda.test.mvvmnotepad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import cz.vitlabuda.test.mvvmnotepad.adapters.NotesRecyclerAdapter;
import cz.vitlabuda.test.mvvmnotepad.arch.Note;
import cz.vitlabuda.test.mvvmnotepad.arch.NoteViewModel;

public class MainActivity extends AppCompatActivity {

    private SnackbarMaker snackbarMaker;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snackbarMaker = new SnackbarMaker(findViewById(android.R.id.content));

        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);

        initializeWidgets();
    }

    private void initializeWidgets() {
        RecyclerView recyclerView = findViewById(R.id.notes_recyclerview);
        NotesRecyclerAdapter.NoteActionListener noteActionListener = new NotesRecyclerAdapter.NoteActionListener() {
            @Override
            public void onClicked(Note note) {
                showAddEditNoteDialog(true, note);
            }

            @Override
            public void onSwiped(Note note) {
                deleteNote(note);
            }
        };

        NotesRecyclerAdapter notesRecyclerAdapter = new NotesRecyclerAdapter(this, recyclerView, noteViewModel, noteActionListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_main_add)
            showAddEditNoteDialog(false, null);
        else if(id == R.id.menu_main_delete_all_notes)
            showDeleteAllNotesConfirmationDialog();

        return super.onOptionsItemSelected(item);
    }

    private void showAddEditNoteDialog(boolean isEdited, @Nullable Note oldNote) {
        String actionString = (isEdited ? "Edit" : "Add");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_note, null, false);
        EditText titleEditText = view.findViewById(R.id.dialog_add_note_title);
        EditText textEditText = view.findViewById(R.id.dialog_add_note_text);


        if(isEdited) {
            assert oldNote != null;
            titleEditText.setText(oldNote.getTitle());
            textEditText.setText(oldNote.getText());

        } else {
            assert oldNote == null;

        }


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(actionString + " note")
                .setView(view)
                .setPositiveButton(actionString, null)
                .setNegativeButton("Cancel", null)
                .show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String text = textEditText.getText().toString().trim();

            if(title.isEmpty()) {
                showErrorDialog("The title field is empty!");
                return;
            }
            if(text.isEmpty()) {
                showErrorDialog("The text field is empty!");
                return;
            }

            if(isEdited)
                editNote(oldNote, title, text);
            else
                addNote(title, text);

            dialog.dismiss();
        });
    }

    private void showDeleteAllNotesConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete all notes?")
                .setMessage("Are you sure you want to delete all notes? This action is irreversible!")
                .setPositiveButton("Yes", (dialog, which) -> deleteAllNotes())
                .setNegativeButton("No", null)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void addNote(String title, String text) {
        Note addedNote = Note.generateAddedNote(title, text);
        noteViewModel.addNote(addedNote);

        snackbarMaker.makeShort("Note added");
    }

    private void editNote(Note oldNote, String newTitle, String newText) {
        Note editedNote = Note.generateEditedNote(oldNote, newTitle, newText);
        noteViewModel.editNote(editedNote);

        snackbarMaker.makeShort("Note edited");
    }

    private void deleteNote(Note note) {
        noteViewModel.deleteNote(note);

        snackbarMaker.makeShort("Note deleted");
    }

    public void deleteAllNotes() {
        noteViewModel.deleteAllNotes();

        snackbarMaker.makeShort("All notes deleted");
    }
}