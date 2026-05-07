package com.example.balonnote.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balonnote.entity.Note;
import com.example.balonnote.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {


    private String TAG = "MYTAG";

    public NoteAdapter() {
        super(itemCallback);
    }

    private static final DiffUtil.ItemCallback<Note> itemCallback = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cell_layout, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = getItem(position);
        Log.d(TAG, "onBindViewHolder: " + note.getTitle());
        holder.titleText.setText(note.getTitle());
        holder.contentText.setText(note.getContent());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日 HH:mm", Locale.CHINA);
        String createTime = simpleDateFormat.format(new Date(note.getCreateTime()));
        holder.createTimeText.setText(createTime);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, contentText, createTimeText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title_text);
            contentText = itemView.findViewById(R.id.content_text);
            createTimeText = itemView.findViewById(R.id.create_text);
        }
    }
}
