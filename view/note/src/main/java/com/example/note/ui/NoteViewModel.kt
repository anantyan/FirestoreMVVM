package com.example.note.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.local.entities.Note
import com.example.core.repository.NoteLocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteLocalRepository
) : ViewModel() {
    private val _getById = MutableLiveData<Note>()
    private val _insert = MutableLiveData<Unit>()
    private val _update = MutableLiveData<Unit>()
    private val _delete = MutableLiveData<Unit>()

    val getById: LiveData<Note> = _getById
    val insert: LiveData<Unit> = _insert
    val update: LiveData<Unit> = _update
    val delete: LiveData<Unit> = _delete

    val getAll = repository.getAll()

    fun getById(id: Int) = viewModelScope.launch {
        _getById.postValue(repository.getById(id))
    }

    fun insert(note: com.example.core.data.local.entities.Note) = viewModelScope.launch {
        _insert.postValue(repository.insert(note))
    }

    fun update(note: com.example.core.data.local.entities.Note) = viewModelScope.launch {
        _update.postValue(repository.update(note))
    }

    fun delete(id: Int) = viewModelScope.launch {
        _delete.postValue(repository.delete(id))
    }
}