package com.example.balonnote.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.balonnote.Entity.Note;
import com.example.balonnote.Repository.NoteRepository;

import java.util.List;

public class NoteViewModel {
    private final NoteRepository repository;

    public NoteViewModel(Context context){
            repository = new NoteRepository(context);
    }

    public void insertNote(Note note){
        repository.insertNote(note);
    }

    public void updateNote(Note note){
        repository.updateNote(note);
    }

    public void deleteNote(Note note){
        repository.deleteNote(note);
    }

    public LiveData<List<Note>> getAllNotes(){
        return repository.getAllNotes();
    }

    public LiveData<List<Note>> getNotesByFolder(long folderId){
        return repository.getNotesByFolder(folderId);
    }

    public LiveData<List<Note>> getNotesUnFolder(){
        return repository.getNotesUnFolder();
    }

    public LiveData<List<Note>> searchNotes(String keywords){
        return repository.searchNotes(keywords);
    }

    public void removeFolder(long noteId){
        repository.removeFolder(noteId);
    }

    public void addFolder(long noteId, long folderId){
        repository.addFolder(noteId, folderId);
    }

    public void moveToTrash(long noteId){
        repository.moveToTrash(noteId);
    }

    public void deleteNoteById(long noteId){
        repository.deleteNoteById(noteId);
    }

}
