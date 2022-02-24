package idv.zijunliao.note.data

import android.content.SearchRecentSuggestionsProvider

@Deprecated("use custom suggestion chips instead of suggestion list, but keep this class as note")
class NoteSuggestionProvider : SearchRecentSuggestionsProvider() {
    companion object {
        const val AUTHORITY = "idv.zijunliao.note.data.NoteSuggestionProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }

    init {
        setupSuggestions(AUTHORITY, MODE)
    }
}