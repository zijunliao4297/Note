package idv.zijunliao.note.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import idv.zijunliao.note.Constants.KEY_FIRST_OPEN
import idv.zijunliao.note.Constants.KEY_ORDER
import idv.zijunliao.note.Constants.KEY_SORT
import idv.zijunliao.note.Constants.KEY_SPAN_COUNT
import idv.zijunliao.note.Constants.KEY_SUGGESTIONS
import idv.zijunliao.note.data.use_case.Order
import idv.zijunliao.note.data.use_case.Sort
import kotlinx.coroutines.flow.*

/**
 * Repository class to access data store
 * access each flow to collect it result and update it with certain function
 */
class DataStoreRepository(private val dataStore: DataStore<Preferences>) {

    /**
     * To check is the first open of application and setup default notes
     * Only setup once on the app open
     * Default value is true
     */
    val firstOpenFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_FIRST_OPEN] ?: true
        }

    /**
     * Saved user query strings as suggestions to initialize the suggestion chips
     */
    val suggestionsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[KEY_SUGGESTIONS] ?: setOf()
        }

    /**
     * Saved sort and order strategy as int.
     * Reverse the int back to sealed class so that layout can initialize with default state
     * @see Sort
     * @see Order
     */
    val sortOrderFlow: Flow<Sort> = dataStore.data
        .map { preferences ->
            val order = when (preferences[KEY_ORDER] ?: 0) {
                1 -> Order.Increasing
                else -> Order.Decreasing
            }
            return@map when (preferences[KEY_SORT] ?: 0) {
                1 -> Sort.Title(order)
                2 -> Sort.Color(order)
                else -> Sort.Timestamp(order)
            }
        }

    /**
     * Saved span count of StaggeredGridLayoutManager
     * Setup menu icon state and RecyclerView by this value every times
     * Default value is 1
     * @see StaggeredGridLayoutManager
     */
    val spanCountFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[KEY_SPAN_COUNT] ?: 1
        }

    /**
     * Methode for append suggestion into datastore
     * Remove the old suggestion when size out of 16
     */
    suspend fun updateSuggestion(suggestion: String) {
        dataStore.edit { preferences ->
            val suggestions = ArrayList(preferences[KEY_SUGGESTIONS] ?: setOf())
            if (!suggestions.contains(suggestion)) {
                while (suggestions.size > 16)
                    suggestions.removeFirst()
                suggestions.add(suggestion)
                preferences[KEY_SUGGESTIONS] = suggestions.toSet()
            }
        }
    }

    /**
     * Method for delete all search suggestions
     */
    suspend fun deleteSuggestion() {
        dataStore.edit { preferences ->
            preferences[KEY_SUGGESTIONS] = setOf()
        }
    }

    /**
     * Method for reset first time open state to false
     */
    suspend fun updateOpenState() {
        dataStore.edit { it[KEY_FIRST_OPEN] = false }
    }

    /**
     * Method for save sort strategy as an int
     */
    suspend fun updateSortType(sort: Sort) {
        dataStore.edit {
            it[KEY_SORT] = when (sort) {
                is Sort.Timestamp -> 0
                is Sort.Title -> 1
                is Sort.Color -> 2
            }
        }
    }

    /**
     * Method for save order strategy as an int
     */
    suspend fun updateOrderType(order: Order) {
        dataStore.edit {
            it[KEY_ORDER] = when (order) {
                Order.Decreasing -> 0
                Order.Increasing -> 1
            }
        }
    }

    /**
     * Method for save span count
     */
    suspend fun updateSpanCount(spanCount: Int) {
        dataStore.edit {
            it[KEY_SPAN_COUNT] = spanCount
        }
    }
}