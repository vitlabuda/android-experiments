package cz.vitlabuda.test.mvvmnotepad.arch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private final String title;

    @NonNull
    private final String text;

    @NonNull
    @ColumnInfo(name = "last_modified")
    private final Date lastModified;

    public Note(@NonNull String title, @NonNull String text, @NonNull Date lastModified) {
        this.title = title;
        this.text = text;
        this.lastModified = lastModified;
    }

    public static Note generateAddedNote(@NonNull String title, @NonNull String text) {
        return new Note(title, text, new Date());
    }

    public static Note generateEditedNote(@NonNull Note oldNote, @Nullable String title, @Nullable String text) {
        Note newNote = new Note(
            (title == null ? oldNote.getTitle() : title),
            (text == null ? oldNote.getText() : text),
            new Date()
        );
        newNote.setId(oldNote.getId());

        return newNote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public Date getLastModified() {
        return lastModified;
    }
}
