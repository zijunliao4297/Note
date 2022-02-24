package idv.zijunliao.note.data.room

import kotlinx.coroutines.flow.Flow
import idv.zijunliao.note.data.use_case.NoteUseCase

/**
 * Repository class to access note dao.
 * @see NoteUseCase to fetch data with filter logic.
 */
class NoteRepository(private val dao: NoteDao) : IRepository<Note> {

    override fun selectAll(): Flow<List<Note>> = dao.getNotes()

    override fun query(keyword: String): Flow<List<Note>> = dao.queryNotes(keyword)

    override suspend fun insert(note: Note) {
        dao.insert(note)
    }

    override suspend fun delete(note: Note) {
        dao.delete(note)
    }

    override suspend fun selectById(id: Int): Note? = dao.getNoteById(id)

    /**
     * Not support this function on current ui.
     */
    override suspend fun deleteAll() {}
}