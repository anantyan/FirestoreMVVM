package com.example.core.repository

import androidx.lifecycle.LiveData
import com.example.core.data.local.dao.NoteDao
import com.example.core.data.local.entities.Note
import javax.inject.Inject

class NoteLocalRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAll(): LiveData<List<Note>> {
        return noteDao.getAll()
    }

    suspend fun getById(id: Int = 0): Note {
        return noteDao.getById(id)
    }

    suspend fun insert(note: Note) {
        return noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        return noteDao.update(note)
    }

    suspend fun delete(id: Int = 0) {
        return noteDao.delete(id)
    }
}