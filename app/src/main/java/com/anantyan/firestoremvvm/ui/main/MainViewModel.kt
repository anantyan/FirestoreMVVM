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

    val read: LiveData<Resource<Note?>> = _read
    val write: LiveData<Resource<Unit>> = _write

    fun getAll() = noteRepository.getAll().cachedIn(CoroutineScope(Dispatchers.IO)).asLiveData()

    fun getById(id: String) = CoroutineScope(Dispatchers.IO).launch {
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

    fun insert(note: Note) = CoroutineScope(Dispatchers.IO).launch {
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

    fun update(id: String, note: Note) = CoroutineScope(Dispatchers.IO).launch {
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

    fun delete(id: String) = CoroutineScope(Dispatchers.IO).launch {
        _write.postValue(Resource.Loading())
        try {
            noteRepository.delete(id)
                .collectLatest {
                    _write.postValue(Resource.Success(it))
                }
        } catch (ex: Exception) {
            _write.postValue(Resource.Error(ex.message))
        }
    }
}