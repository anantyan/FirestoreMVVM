package com.example.core

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.data.local.dao.NoteDao
import com.example.core.data.local.database.NoteDatabase
import com.example.core.data.local.entities.Note
import com.example.core.utils.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteDaoTest {

    @get:Rule
    val instantTastExecutorRule = InstantTaskExecutorRule()

    lateinit var noteDatabase: NoteDatabase
    lateinit var noteDao: NoteDao

    @Before
    fun setUp() {
        noteDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries().build()
        noteDao = noteDatabase.noteDao()
    }

    @Test
    fun testGetAllWhenNotEmpty() = runBlocking {
        val note = Note(title = "Title1", content = "Content1", publish = true)
        val note2 = Note(title = "Title1", content = "Content1", publish = true)
        noteDao.insert(note)
        noteDao.insert(note2)

        val allNotes = noteDao.getAll().getOrAwaitValue()

        assertNotNull(allNotes)
        assertEquals(2, allNotes.size)
        assertTrue(allNotes.contains(note.copy(id = 1)) && allNotes.contains(note2.copy(id = 2)))
    }

    @Test
    fun testGetAllWhenEmpty() {
        val allNotes = noteDao.getAll().getOrAwaitValue()

        assertNotNull(allNotes)
        assertTrue(allNotes.isEmpty())
    }

    @Test
    fun testInsertSuccess() = runBlocking {
        val note = Note(title = "Title1", content = "Content1", publish = true)
        val id = 1
        noteDao.insert(note)
        val insertedNote = noteDao.getById(id)

        assertNotNull(insertedNote)
        assertEquals(note.title, insertedNote.title)
        assertEquals(note.content, insertedNote.content)
        assertEquals(note.publish, insertedNote.publish)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun testInsertDuplicateError() = runBlocking {
        val note = Note(id = 1, title = "Title1", content = "Content1", publish = true)
        noteDao.insert(note)
        noteDao.insert(note)
    }

    @Test
    fun testUpdateSuccess() = runBlocking {
        val originalNote = Note(title = "Title1", content = "Content1", publish = true)
        noteDao.insert(originalNote)

        val updatedNote = originalNote.copy(id = 1, title = "TitleUpdated", content = "ContentUpdated")
        noteDao.update(updatedNote)

        val retrievedNote = noteDao.getAll().getOrAwaitValue()
        assertTrue(retrievedNote.isNotEmpty())
        assertEquals(updatedNote.title, retrievedNote[0].title)
        assertEquals(updatedNote.content, retrievedNote[0].content)
    }

    @Test
    fun testUpdateNonExistentNote() = runBlocking {
        val nonExistentNote = Note(title = "NonExistent", content = "Doesn't exist in DB", publish = false)
        noteDao.update(nonExistentNote)

        val retrievedNotes = noteDao.getAll().getOrAwaitValue()
        assertTrue(retrievedNotes.isEmpty())
    }

    @Test
    fun testDeleteSuccess() = runBlocking {
        val noteToBeDeleted = Note(title = "Title1", content = "Content1", publish = true)
        val id = 1
        noteDao.insert(noteToBeDeleted)

        noteDao.delete(id)

        val retrievedNotes = noteDao.getAll().getOrAwaitValue()
        assertTrue(retrievedNotes.isEmpty())
    }


    @Test
    fun testDeleteNonExistentNote() = runBlocking {
        val id = 1
        val initialNotes = noteDao.getAll().getOrAwaitValue()
        noteDao.delete(id)

        val retrievedNotesAfterDelete = noteDao.getAll().getOrAwaitValue()
        assertEquals(initialNotes.size, retrievedNotesAfterDelete.size)
    }


    @After
    fun tearDown() {
        noteDatabase.close()
    }
}