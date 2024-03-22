package com.example.kotlin14

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE contact_id = :contactId")
    fun getNotesByContactId(contactId: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE contact_id = :contactId")
    fun getNoteByContactId(contactId: String): List<Note>
}