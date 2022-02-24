package idv.zijunliao.note.data.use_case

import idv.zijunliao.note.data.room.NoteDao

sealed class NoteListEvent {

    /**
     * Inverse the ordinate and save this change
     * @see Order
     */
    object Reorder : NoteListEvent()

    /**
     * Reset a new sort strategy and save this change
     * @see Sort
     */
    data class Resort(val sort: Sort) : NoteListEvent()

    /**
     * Restore the delete note by the temp reference
     */
    object Restore : NoteListEvent()

    /**
     * Delete the note by a index from viewHolder adapter position
     */
    class Delete(val index: Int) : NoteListEvent()

    /**
     * Change the recycler view layout and save this change
     */
    class ChangeSpanCount(val spanCount: Int) : NoteListEvent()
}
