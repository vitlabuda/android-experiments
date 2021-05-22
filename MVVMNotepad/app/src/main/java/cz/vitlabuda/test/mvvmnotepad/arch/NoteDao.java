package cz.vitlabuda.test.mvvmnotepad.arch;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY last_modified DESC")
    LiveData<List<Note>> getNotes();

    /**
      * @param addedNote Should be generated using Note.generateAddedNote()
     */
    @Insert
    void add(Note addedNote);

    /**
     * @param editedNote Should be generated using Note.generateEditedNote()
     */
    @Update
    void edit(Note editedNote);

    @Delete
    void delete(Note deletedNote);

    @Query("DELETE FROM notes")
    void deleteAll();
}
