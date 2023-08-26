package com.example.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.core.data.local.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM tbl_note")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM tbl_note WHERE id=:id")
    suspend fun getById(id: Int = 0): Note

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("DELETE FROM tbl_note WHERE id=:id")
    suspend fun delete(id: Int = 0)
}