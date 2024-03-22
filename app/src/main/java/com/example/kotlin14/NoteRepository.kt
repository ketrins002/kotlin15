package com.example.kotlin14

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(context: Context) {
    private val noteDao: NoteDao
    private var allNotes: LiveData<List<Note>>

    init {
        val db = AppDatabase.getDatabase(context)
        noteDao = db.noteDao()
        allNotes = noteDao.getAllNotes()
    }

    fun getNotesByContactId(contactId: String): LiveData<List<Note>> {
        return noteDao.getNotesByContactId(contactId)
    }

    suspend fun insert(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.insert(note)
        }
    }

    suspend fun update(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.update(note)
        }
    }
}
