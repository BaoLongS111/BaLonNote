package com.example.balonnote.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.balonnote.DAO.FolderDAO;
import com.example.balonnote.DAO.NoteDAO;
import com.example.balonnote.Entity.Folder;
import com.example.balonnote.Entity.Note;

@Database(entities = {Note.class, Folder.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static volatile NoteDatabase instance;

    public static NoteDatabase getDatabase(Context context) {
        if (instance == null) {
            synchronized (NoteDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    NoteDatabase.class,
                                    "note_database")
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract NoteDAO getNoteDAO();
    public abstract FolderDAO getFolderDAO();

}
