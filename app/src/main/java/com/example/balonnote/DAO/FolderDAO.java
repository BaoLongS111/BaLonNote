package com.example.balonnote.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.balonnote.Entity.Folder;

import java.util.List;

@Dao
public interface FolderDAO {
    // 新增分组
    @Insert
    void insertFolder(Folder folder);

    // 更新分组
    @Update
    void updateFolder(Folder folder);

    // 删除分组
    @Delete
    void deleteFolder(Folder folder);

    // 获取所有分组，按创建时间排序
    @Query("SELECT * FROM folder_table ORDER BY sort_order ASC , create_time ASC")
    LiveData<List<Folder>> getAllFolders();

    // 按ID获取分组
    @Query("SELECT * FROM folder_table WHERE id = :folderId")
    LiveData<Folder> getFolderById(long folderId);

}
