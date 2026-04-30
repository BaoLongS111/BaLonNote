package com.example.balonnote.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.balonnote.Entity.Folder;
import com.example.balonnote.Repository.FolderRepository;

import java.util.List;

public class FolderViewModel {
    private final FolderRepository repository;

    public FolderViewModel(Context context){
        repository = new FolderRepository(context);
    }

    public void insertFolder(Folder folder){
        repository.insertFolder(folder);
    }

    public void deleteFolder(Folder folder){
        repository.deleteFolder(folder);
    }

    public void updateFolder(Folder folder){
        repository.updateFolder(folder);
    }

    public LiveData<List<Folder>> getAllFolders(){
        return repository.getAllFolders();
    }

    public LiveData<Folder> getFolderById(long folderId){
        return repository.getFolderById(folderId);
    }
}
