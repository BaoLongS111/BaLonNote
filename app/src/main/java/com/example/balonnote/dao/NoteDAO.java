package com.example.balonnote.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.balonnote.entity.Note;
import java.util.List;

@Dao
public interface NoteDAO {

    // 插入笔记
    @Insert
    void insertNote(Note note);
    // 更新笔记
    @Update
    void updateNote(Note note);
    // 删除笔记
    @Delete
    void deleteNote(Note note);

    // 获取所有笔记，按置顶和创建时间降序
    @Query("SELECT * FROM note_table WHERE is_deleted = 0 ORDER BY is_pinned DESC , update_time DESC")
    LiveData<List<Note>> getAllNotes();

    // 按分组获取笔记
    @Query("SELECT * FROM note_table WHERE is_deleted = 0 AND folder_id = :folderId ORDER BY is_pinned DESC , update_time DESC")
    LiveData<List<Note>> getNotesByFolder(long folderId);

    // 获取所有没有分组的笔记
    @Query("SELECT * FROM note_table WHERE is_deleted = 0 AND folder_id IS NULL ORDER BY update_time DESC")
    LiveData<List<Note>> getNotesUnFolder();

    // 模糊查找笔记
    @Query("SELECT * FROM note_table WHERE (title LIKE :keywords OR content LIKE :keywords) AND is_deleted = 0 ORDER BY is_pinned DESC , update_time DESC")
    LiveData<List<Note>> searchNotes(String keywords);

    // 把笔记移除分组
    @Query("UPDATE note_table SET folder_id = NULL WHERE id = :noteId")
    void removeFolder(long noteId);

    // 把笔记添加到分组
    @Query("UPDATE note_table SET folder_id = :folderId WHERE id = :noteId")
    void addFolder(long noteId,long folderId);

    // 把笔记移到垃圾桶
    @Query("UPDATE note_table SET is_deleted = 1 WHERE id = :noteId")
    void moveToTrash(long noteId);

    // 删除笔记（无法找回）
    @Query("DELETE FROM note_table WHERE id = :noteId")
    void deleteNoteById(long noteId);

}
