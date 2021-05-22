package cz.vitlabuda.test.mvvmnotepad.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.vitlabuda.test.mvvmnotepad.R;
import cz.vitlabuda.test.mvvmnotepad.arch.Note;
import cz.vitlabuda.test.mvvmnotepad.arch.NoteViewModel;

public final class NotesRecyclerAdapter extends ListAdapter<Note, NotesRecyclerAdapter.NotesRecyclerViewHolder> {
    public interface NoteActionListener {
        void onClicked(Note note);
        void onSwiped(Note note);
    }

    public static final class NotesRecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView textTextView;
        private final TextView lastModifiedTextView;

        public NotesRecyclerViewHolder(@NonNull View itemView, NotesRecyclerAdapter adapter) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position < 0)
                    return;

                adapter.getNoteActionListener().onClicked(adapter.getItem(position));
            });

            this.titleTextView = itemView.findViewById(R.id.recycleritem_note_title);
            this.textTextView = itemView.findViewById(R.id.recycleritem_note_text);
            this.lastModifiedTextView = itemView.findViewById(R.id.recycleritem_note_lastmodified);
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getTextTextView() {
            return textTextView;
        }

        public TextView getLastModifiedTextView() {
            return lastModifiedTextView;
        }
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return (oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getText().equals(newItem.getText()) &&
                    oldItem.getLastModified().getTime() == newItem.getLastModified().getTime());
        }
    };

    private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private final NoteActionListener noteActionListener;

    public NotesRecyclerAdapter(AppCompatActivity activity, RecyclerView recyclerView, NoteViewModel noteViewModel, NoteActionListener noteActionListener) {
        super(DIFF_CALLBACK);
        this.noteActionListener = noteActionListener;

        createAndAttachItemTouchHelper(recyclerView);

        noteViewModel.getAllNotes().observe(activity, this::submitList);
    }

    private void createAndAttachItemTouchHelper(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if(position < 0)
                    return;

                noteActionListener.onSwiped(getItem(position));
            }
        }).attachToRecyclerView(recyclerView);
    }

    public NoteActionListener getNoteActionListener() {
        return noteActionListener;
    }

    @NonNull
    @Override
    public NotesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_note, parent, false);

        return new NotesRecyclerViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesRecyclerAdapter.NotesRecyclerViewHolder holder, int position) {
        Note note = getItem(position);

        holder.getTitleTextView().setText(note.getTitle());
        holder.getTextTextView().setText(note.getText());
        holder.getLastModifiedTextView().setText(DATE_FORMATTER.format(note.getLastModified()));
    }
}
