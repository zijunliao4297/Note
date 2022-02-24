package idv.zijunliao.note.data.use_case

sealed class NoteState {
    object InsertSuccess : NoteState()
    class  InsertInvalid(val message: String?) : NoteState()
}