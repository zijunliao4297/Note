package idv.zijunliao.note.data.use_case

import idv.zijunliao.note.data.room.NoteDao

sealed class NoteFindEvent {

    /**
     * Search note database by the query string
     * @see NoteDao
     */
    class Search(val query: String) : NoteFindEvent()

    /**
     * Delete all suggestion in the datastore
     */
    object ClearSuggestion : NoteFindEvent()

    /**
     * Clear search result to reset ui
     * There are two situations will trigger this event:
     * 1-> back to list after edit.
     * 2-> back to list by navigation up.
     */
    object ClearSearchResult : NoteFindEvent()
}
