package idv.zijunliao.note.data.use_case

import idv.zijunliao.note.data.room.IRepository
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.room.NoteInvalidException
import kotlinx.coroutines.flow.map

/**
 * Class to access database repository with filter logic or exception.
 * So that can only test on this class with dummy data repository.
 */
class NoteUseCase(private val repository: IRepository<Note>) {
    fun getNotes(sort: Sort) = repository
        .selectAll()
        .map { list ->
            return@map when (sort.order) {
                Order.Decreasing -> {
                    when (sort) {
                        is Sort.Color -> list.sortedByDescending { it.color }
                        is Sort.Timestamp -> list.sortedByDescending { it.timestamp }
                        is Sort.Title -> list.sortedByDescending { it.title }
                    }
                }
                Order.Increasing -> {
                    when (sort) {
                        is Sort.Color -> list.sortedBy { it.color }
                        is Sort.Timestamp -> list.sortedBy { it.timestamp }
                        is Sort.Title -> list.sortedBy { it.title }
                    }
                }
            }
        }

    fun queryNotes(keyword: String) = repository.query(keyword)

    @Throws(NoteInvalidException::class)
    suspend fun insertNote(note: Note) {
        if (note.title.isBlank()) throw NoteInvalidException("Note title cannot be empty.")
        if (note.content.isBlank()) throw NoteInvalidException("Note content cannot be empty.")
        repository.insert(note)
    }

    suspend fun deleteNote(note: Note) {
        repository.delete(note)
    }
}
