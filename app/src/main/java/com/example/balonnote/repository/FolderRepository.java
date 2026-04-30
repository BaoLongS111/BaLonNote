package com.example.balonnote.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.balonnote.dao.FolderDAO;
import com.example.balonnote.database.NoteDatabase;
import com.example.balonnote.entity.Folder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FolderRepository {
    private FolderDAO folderDAO;
    private NoteDatabase database;
    private ExecutorService executorService;

    public FolderRepository(Context context){
        database = NoteDatabase.getDatabase(context);
        folderDAO = database.getFolderDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertFolder(Folder folder){
        executorService.execute(()-> folderDAO.insertFolder(folder));
    }

    public void deleteFolder(Folder folder){
        executorService.execute(()-> folderDAO.deleteFolder(folder));
    }

    public void updateFolder(Folder folder){
        executorService.execute(()-> folderDAO.updateFolder(folder));
    }

    public LiveData<List<Folder>> getAllFolders(){
        return folderDAO.getAllFolders();
    }

    public LiveData<Folder> getFolderById(long folderId){
        return folderDAO.getFolderById(folderId);
    }


}
