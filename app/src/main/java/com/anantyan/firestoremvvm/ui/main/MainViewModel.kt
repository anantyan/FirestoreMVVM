package com.anantyan.firestoremvvm.ui.main

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.anantyan.firestoremvvm.model.Note
import com.anantyan.firestoremvvm.repository.NoteRepository
import com.anantyan.firestoremvvm.utils.LiveEvent
import com.anantyan.firestoremvvm.utils.LiveEventConfig
import com.anantyan.firestoremvvm.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _read = LiveEvent<Resource<Note?>>(LiveEventConfig.PreferFirstObserver)
    private val _write = LiveEvent<Resource<Unit>>(LiveEventConfig.PreferFirstObserver)
    private val _delete = LiveEvent<Resource<Unit>>(LiveEventConfig.PreferFirstObserver)

    val read: LiveData<Resource<Note?>> = _read
    val write: LiveData<Resource<Unit>> = _write
    val delete: LiveData<Resource<Unit>> = _delete

    fun getAll() = noteRepository.getAll().cachedIn(viewModelScope).asLiveData()

    fun getById(id: String) = viewModelScope.launch {
        _read.postValue(Resource.Loading())
        try {
            noteRepository.getById(id)
                .collectLatest {
                    _read.postValue(Resource.Success(it))
                }
        } catch (ex: Exception) {
            _read.postValue(Resource.Error(ex.message))
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        _write.postValue(Resource.Loading())
        try {
            noteRepository.insert(note)
                .collectLatest {
                    _write.postValue(Resource.Success(it))
                }
        } catch (ex: Exception) {
            _write.postValue(Resource.Error(ex.message))
        }
    }

    fun update(id: String, note: Note) = viewModelScope.launch {
        _write.postValue(Resource.Loading())
        try {
            noteRepository.update(id, note)
                .collectLatest {
                    _write.postValue(Resource.Success(it))
                }
        } catch (ex: Exception) {
            _write.postValue(Resource.Error(ex.message))
        }
    }

    fun delete(id: String) = viewModelScope.launch {
        _delete.postValue(Resource.Loading())
        try {
            noteRepository.delete(id)
                .collectLatest {
                    _delete.postValue(Resource.Success(it))
                }
        } catch (ex: Exception) {
            _delete.postValue(Resource.Error(ex.message))
        }
    }
}