package com.anantyan.firestoremvvm.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.anantyan.firestoremvvm.data.firestore.create
import com.anantyan.firestoremvvm.data.firestore.delete
import com.anantyan.firestoremvvm.data.firestore.read
import com.anantyan.firestoremvvm.data.firestore.update
import com.anantyan.firestoremvvm.model.Note
import com.anantyan.firestoremvvm.utils.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface NotePresenter {
    fun getAll(): Flow<PagingData<Note>>
    fun getById(id: String): Flow<Resource<Note>>
    fun insert(note: Note): Flow<Resource<Unit>>
    fun update(id: String, note: Note): Flow<Resource<Unit>>
    fun delete(id: String): Flow<Resource<Unit>>
}

class NoteRepository @Inject constructor() : NotePresenter {

    private val db = Firebase.firestore

    override fun getAll() = Pager(
        config = PagingConfig(pageSize = 15),
        pagingSourceFactory = {
            NotePagingSource(db)
        }
    ).flow

    override fun getById(id: String): Flow<Resource<Note>> = flow {
        emit(Resource.Loading())
        val note = db.read(id)
        emit(Resource.Success(note ?: Note()))
    }.catch { error(Resource.Error<Note>(it.message ?: "Terjadi kesalahan!")) }

    override fun insert(note: Note): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val unit = db.create(note)
        emit(Resource.Success(unit))
    }.catch { error(Resource.Error<Unit>(it.message ?: "Terjadi kesalahan!")) }

    override fun update(id: String, note: Note): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val unit = db.update(id, note)
        emit(Resource.Success(unit))
    }.catch { error(Resource.Error<Unit>(it.message ?: "Terjadi kesalahan!")) }

    override fun delete(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val unit = db.delete(id)
        emit(Resource.Success(unit))
    }.catch { error(Resource.Error<Unit>(it.message ?: "Terjadi kesalahan!")) }
}