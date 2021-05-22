package cz.vitlabuda.test.mvvmnotepad.arch;

import android.os.AsyncTask;

public class DatabaseOperationAsyncTasks {
    public static abstract class NoteAsyncTaskBase<Params> extends AsyncTask<Params, Void, Void> {
        protected final NoteDao noteDao;

        public NoteAsyncTaskBase(NoteDao noteDao) {
            super();
            this.noteDao = noteDao;
        }
    }

    public static final class AddNoteAsyncTask extends NoteAsyncTaskBase<Note> {
        public AddNoteAsyncTask(NoteDao noteDao) {
            super(noteDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.add(notes[0]);
            return null;
        }
    }

    public static final class EditNoteAsyncTask extends NoteAsyncTaskBase<Note> {
        public EditNoteAsyncTask(NoteDao noteDao) {
            super(noteDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.edit(notes[0]);
            return null;
        }
    }

    public static final class DeleteNoteAsyncTask extends NoteAsyncTaskBase<Note> {
        public DeleteNoteAsyncTask(NoteDao noteDao) {
            super(noteDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    public static final class DeleteAllNotesAsyncTask extends NoteAsyncTaskBase<Void> {
        public DeleteAllNotesAsyncTask(NoteDao noteDao) {
            super(noteDao);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAll();
            return null;
        }
    }

    private DatabaseOperationAsyncTasks() {}
}
