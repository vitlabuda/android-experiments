package cz.vitlabuda.test.mvvmnotepad.arch;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        this.noteDao = database.noteDao();
    }

    public LiveData<List<Note>> getNotes() {
        return noteDao.getNotes();
    }

    /**
     * @param addedNote Should be generated using Note.generateAddedNote()
     */
    public void add(Note addedNote) {
        new DatabaseOperationAsyncTasks.AddNoteAsyncTask(noteDao).execute(addedNote);
    }

    /**
     * @param editedNote Should be generated using Note.generateEditedNote()
     */
    public void edit(Note editedNote) {
        new DatabaseOperationAsyncTasks.EditNoteAsyncTask(noteDao).execute(editedNote);
    }

    public void delete(Note note) {
        new DatabaseOperationAsyncTasks.DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAll() {
        new DatabaseOperationAsyncTasks.DeleteAllNotesAsyncTask(noteDao).execute();
    }
}
