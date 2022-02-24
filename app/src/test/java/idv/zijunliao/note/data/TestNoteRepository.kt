package idv.zijunliao.note.data

import idv.zijunliao.note.data.room.IRepository
import idv.zijunliao.note.data.room.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestNoteRepository : IRepository<Note> {
    private val notes = mutableListOf<Note>()

    override fun selectAll(): Flow<List<Note>> = flow { emit(notes) }

    override fun query(keyword: String): Flow<List<Note>> =
        flow { emit(notes.filter { it.title.contains(keyword) || it.content.contains(keyword) }) }

    override suspend fun selectById(id: Int): Note? = notes.find { it.id == id }

    override suspend fun insert(note: Note) {
        notes.add(note)
    }

    override suspend fun delete(note: Note) {
        notes.remove(note)
    }

    override suspend fun deleteAll() {
        notes.clear()
    }
}