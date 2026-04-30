package com.example.balonnote.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.balonnote.DAO.NoteDAO;
import com.example.balonnote.Database.NoteDatabase;
import com.example.balonnote.Entity.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDAO noteDAO;
    private final ExecutorService executorService;


    public NoteRepository(Context context) {
        NoteDatabase database = NoteDatabase.getDatabase(context);
        noteDAO = database.getNoteDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertNote(Note note){
        executorService.execute(()-> noteDAO.insertNote(note));
    }

    public void updateNote(Note note){
        executorService.execute(()-> noteDAO.updateNote(note));
    }

    public void deleteNote(Note note){
        executorService.execute(()-> noteDAO.deleteNote(note));
    }

    public LiveData<List<Note>> getAllNotes(){
        return noteDAO.getAllNotes();
    }

    public LiveData<List<Note>> getNotesByFolder(long folderId){
        return noteDAO.getNotesByFolder(folderId);
    }

    public LiveData<List<Note>> getNotesUnFolder(){
        return noteDAO.getNotesUnFolder();
    }

    public LiveData<List<Note>> searchNotes(String keywords){
        return noteDAO.searchNotes(keywords);
    }

    public void removeFolder(long noteId){
        executorService.execute(()-> noteDAO.removeFolder(noteId));
    }

    public void addFolder(long noteId,long folderId){
        executorService.execute(()-> noteDAO.addFolder(noteId,folderId));
    }

    public void moveToTrash(long noteId){
        executorService.execute(()-> noteDAO.moveToTrash(noteId));
    }

    public void deleteNoteById(long noteId){
        executorService.execute(()-> noteDAO.deleteNoteById(noteId));
    }



}
