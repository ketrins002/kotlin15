package com.example.kotlin14

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(context: Context) : ViewModel() {

    private val repository: NoteRepository = NoteRepository(context)

    fun getNotesByContactId(id: String): LiveData<List<Note>> = repository.getNotesByContactId(id)

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }
}
