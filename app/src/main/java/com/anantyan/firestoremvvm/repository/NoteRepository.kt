package com.anantyan.firestoremvvm.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.anantyan.firestoremvvm.data.firestore.create
import com.anantyan.firestoremvvm.data.firestore.delete
import com.anantyan.firestoremvvm.data.firestore.read
import com.anantyan.firestoremvvm.data.firestore.update
import com.anantyan.firestoremvvm.model.Note
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoteRepository @Inject constructor() {

    private val db = Firebase.firestore
    /*private val user = Firebase.auth.currentUser*/

    fun getAll() = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            NotePagingSource(db)
        }
    ).flow

    fun getById(id: String): Flow<Note?> = flow {
        try {
            val note = db.read(id)
            emit(note)
        } catch (ex: Exception) {
            error(ex.message ?: "Terjadi kesalahan!")
        }
    }

    fun insert(note: Note): Flow<Unit> = flow {
        try {
            val unit = db.create(note)
            emit(unit)
        } catch (ex: Exception) {
            error(ex.message ?: "Terjadi kesalahan!")
        }
    }

    fun update(id: String, note: Note): Flow<Unit> = flow {
        try {
            val unit = db.update(id, note)
            emit(unit)
        } catch (ex: Exception) {
            error(ex.message ?: "Terjadi kesalahan!")
        }
    }

    fun delete(id: String): Flow<Unit> = flow {
        try {
            val unit = db.delete(id)
            emit(unit)
        } catch (ex: Exception) {
            error(ex.message ?: "Terjadi kesalahan!")
        }
    }
}