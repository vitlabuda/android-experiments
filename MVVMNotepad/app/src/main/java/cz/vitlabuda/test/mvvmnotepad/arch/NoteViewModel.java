package cz.vitlabuda.test.mvvmnotepad.arch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository repository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new NoteRepository(application);
    }

    public LiveData<List<Note>> getAllNotes() {
        return repository.getNotes();
    }

    /**
     * @param addedNote Should be generated using Note.generateAddedNote()
     */
    public void addNote(Note addedNote) {
        repository.add(addedNote);
    }

    /**
     * @param editedNote Should be generated using Note.generateEditedNote()
     */
    public void editNote(Note editedNote) {
        repository.edit(editedNote);
    }

    public void deleteNote(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAll();
    }
}
