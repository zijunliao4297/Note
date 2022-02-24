package idv.zijunliao.note.data.use_case

import android.content.res.Resources
import idv.zijunliao.note.data.room.Note

sealed class NoteEditEvent {

    /**
     * Add an existed note into database
     */
    class AddNote(val note: Note) : NoteEditEvent()

    /**
     * Add a new note into database
     */
    object AddNewNote: NoteEditEvent()

    /**
     * Add introduction notes when application first open.
     */
    class FirstOpen(val resources: Resources) : NoteEditEvent()
}