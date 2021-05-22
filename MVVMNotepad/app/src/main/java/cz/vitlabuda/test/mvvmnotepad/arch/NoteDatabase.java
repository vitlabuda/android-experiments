package cz.vitlabuda.test.mvvmnotepad.arch;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = Note.class, version = 1)
@TypeConverters(Converters.class)
public abstract class NoteDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "note_database.db";

    private static NoteDatabase instance = null; // This class is a singleton.

    public static synchronized NoteDatabase getInstance(Context context) {
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();

        return instance;
    }

    public abstract NoteDao noteDao();
}
