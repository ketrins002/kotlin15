package com.example.kotlin14

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin14.databinding.ActivityMainBinding
import com.example.kotlin14.databinding.EditNoteDialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var vm: NoteViewModel
    private lateinit var b: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                loadContacts()
            } else {
                Toast.makeText(this, "Не удалось получить доступ к контактам", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupContactsRecyclerView()
        checkPermissionsAndLoadContacts()

        val factory = NoteViewModelFactory(this)
        vm = ViewModelProvider(this, factory)[NoteViewModel::class.java]
    }

    private fun setupContactsRecyclerView() {
        b.recyclerViewContacts.layoutManager = LinearLayoutManager(this)
        contactsAdapter = ContactsAdapter { contactId ->
            showEditNoteDialog(contactId)
        }
        b.recyclerViewContacts.adapter = contactsAdapter
    }

    private fun checkPermissionsAndLoadContacts() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) -> {
                loadContacts()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun loadContacts() {
        val contactsList = mutableListOf<Contact>()
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex)
                contactsList.add(Contact(id, name))
            }
        }
        contactsAdapter.submitList(contactsList)
    }

    private fun showEditNoteDialog(contactId: String) {
        val dialogBinding = EditNoteDialogBinding.inflate(layoutInflater)
        var shown = false
        vm.getNotesByContactId(contactId).observe(this) { notes ->
            if (!shown) {
                val note = notes.firstOrNull()
                Log.d("MainActivity:showEditNoteDialog", note.toString())
                dialogBinding.editTextNote.setText(note?.text ?: "")
                val dialog = AlertDialog.Builder(this)
                    .setView(dialogBinding.root)
                    .setTitle(if (note == null) "Новая заметка" else "Редактирование заметки")
                    .setPositiveButton("Сохранить") { _, _ ->
                        val noteText = dialogBinding.editTextNote.text.toString()
                        if (note == null) {
                            vm.insert(Note(contactId = contactId, text = noteText))
                        } else {
                            vm.update(note.copy(text = noteText))
                        }
                        Toast.makeText(this@MainActivity, "Заметка сохранена", Toast.LENGTH_SHORT).show()
                    }.setNegativeButton("Отменить", null).create()
                dialog.show()
                shown = true
            }
        }
    }
}
