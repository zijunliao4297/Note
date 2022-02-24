package idv.zijunliao.note

import idv.zijunliao.note.data.*
import idv.zijunliao.note.data.use_case.NoteUseCase
import idv.zijunliao.note.data.use_case.Order
import idv.zijunliao.note.data.use_case.Sort
import idv.zijunliao.note.data.room.Note
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.hamcrest.Matchers.*
import kotlin.math.sqrt

class SortTest {

    private val repository = TestNoteRepository()
    private val case = NoteUseCase(repository)

    @Before
    fun setup() {
        val notes = mutableListOf<Note>()
        ('a'..'z').forEachIndexed { index, c ->
            notes.add(Note(
                title = c.toString(),
                content = c.toString(),
                timestamp = index.toLong(),
                color = index
            ))
        }
        notes.shuffle()
        runBlocking {
            notes.forEach { note ->
                repository.insert(note)
            }
        }
    }

    @Test
    fun `Sort by title increasing`() = runBlocking {
        case.getNotes(Sort.Title(Order.Increasing))
            .collect { notes ->
                for (i in 0..notes.size - 2) {
                    MatcherAssert.assertThat(notes[i].title, lessThan(notes[i + 1].title))
                }
            }
    }

    @Test
    fun `Sort by timestamp increasing`() = runBlocking {
        case.getNotes(Sort.Timestamp(Order.Increasing))
            .collect { notes ->
                for (i in 0..notes.size - 2) {
                    MatcherAssert.assertThat(notes[i].timestamp, lessThan(notes[i + 1].timestamp))
                }
            }
    }

    @Test
    fun `Sort by color increasing`() = runBlocking {
        case.getNotes(Sort.Color(Order.Increasing))
            .collect { notes ->
                for (i in 0..notes.size - 2) {
                    MatcherAssert.assertThat(notes[i].color, lessThan(notes[i + 1].color))
                }
            }
    }

    @Test
    fun `Sort by title decreasing`() = runBlocking {
        case.getNotes(Sort.Title(Order.Decreasing))
            .collect { notes ->
                for (i in 0..notes.size - 2) {
                    MatcherAssert.assertThat(notes[i].title, greaterThan(notes[i + 1].title))
                }
            }
    }

    @Test
    fun `Sort by timestamp decreasing`() = runBlocking {
        case.getNotes(Sort.Timestamp(Order.Decreasing))
            .collect { notes ->
                for (i in 0..notes.size - 2) {
                    MatcherAssert.assertThat(notes[i].timestamp, greaterThan(notes[i + 1].timestamp))
                }
            }
    }

    @Test
    fun `Sort by color decreasing`() = runBlocking {
        case.getNotes(Sort.Color(Order.Decreasing))
            .collect { notes ->
                for (i in 0..notes.size - 2) {
                    MatcherAssert.assertThat(notes[i].color, greaterThan(notes[i + 1].color))
                }
            }
    }
}