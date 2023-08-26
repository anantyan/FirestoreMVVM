package com.example.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.common.utils.LiveEvent
import com.example.common.utils.LiveEventConfig
import com.example.common.utils.Resource
import com.example.core.data.model.Note
import com.example.core.repository.LocationRepository
import com.example.core.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _currentLocation = MutableLiveData<Resource<Boolean>>()
    private val _read = LiveEvent<Resource<Note>>(LiveEventConfig.PreferFirstObserver)
    private val _write = LiveEvent<Resource<Unit>>(LiveEventConfig.PreferFirstObserver)
    private val _delete = LiveEvent<Resource<Unit>>(LiveEventConfig.PreferFirstObserver)

    val currentLocation: LiveData<Resource<Boolean>> = _currentLocation
    val read: LiveData<Resource<Note>> = _read
    val write: LiveData<Resource<Unit>> = _write
    val delete: LiveData<Resource<Unit>> = _delete

    fun currentLocation() = viewModelScope.launch {
        locationRepository.isUserAtSpecificLocation().collectLatest {
            _currentLocation.postValue(it)
        }
    }

    fun getAll() = _currentLocation.switchMap {
        when (it) {
            is Resource.Loading -> getEmptyPagingData()
            is Resource.Success -> handleSuccessResource(it.data)
            is Resource.Error -> getEmptyPagingData()
        }
    }

    fun getById(id: String) = viewModelScope.launch {
        noteRepository.getById(id).collectLatest {
            _read.postValue(it)
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        noteRepository.insert(note).collectLatest {
            _write.postValue(it)
        }
    }

    fun update(id: String, note: Note) = viewModelScope.launch {
        noteRepository.update(id, note).collectLatest {
            _write.postValue(it)
        }
    }

    fun delete(id: String) = viewModelScope.launch {
        noteRepository.delete(id).collectLatest {
            _delete.postValue(it)
        }
    }

    private fun handleSuccessResource(data: Boolean): LiveData<PagingData<Note>> {
        return if (data) {
            getEmptyPagingData()
        } else {
            noteRepository.getAll().cachedIn(viewModelScope).asLiveData()
        }
    }

    private fun getEmptyPagingData(): MutableLiveData<PagingData<Note>> {
        return MutableLiveData(PagingData.empty())
    }
}