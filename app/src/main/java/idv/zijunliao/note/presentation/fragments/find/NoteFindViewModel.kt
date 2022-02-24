package idv.zijunliao.note.presentation.fragments.find

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.zijunliao.note.Constants
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.store.DataStoreRepository
import idv.zijunliao.note.data.use_case.NoteFindEvent
import idv.zijunliao.note.data.use_case.NoteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteFindViewModel @Inject constructor(private val case: NoteUseCase, private val pref: DataStoreRepository) : ViewModel() {

    /**
     * job for search notes in coroutine
     * cancel it before searching notes to avoid multiple call
     */
    private var mSearchJob: Job? = null

    /**
     * search suggestions from dataStore
     */
    val suggestions = pref.suggestionsFlow.asLiveData()

    /**
     * search result of query string
     */
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    /**
     * query string to update search view by click
     */
    val queryString = MutableLiveData("")

    /**
     * Check previous result to prevent animation run again in the same result.
     * To improve animation run more smoothly by delay 1ms and clear focus.
     */
    private var _nothing = false
    val nothing = MutableLiveData(_nothing)

    fun onEventHandle(event: NoteFindEvent) {
        when (event) {
            NoteFindEvent.ClearSuggestion -> {
                viewModelScope.launch {
                    pref.deleteSuggestion()
                }
            }
            is NoteFindEvent.Search -> {
                val query = event.query.trimEnd()
                queryString.postValue(query)
                mSearchJob?.cancel()
                mSearchJob = case.queryNotes(query)
                    .onEach { list ->
                        if (_nothing != list.isEmpty()) {
                            nothing.postValue(list.isEmpty())
                        }
                        delay(1)
                        _nothing = list.isEmpty()
                        _notes.postValue(list)
                        pref.updateSuggestion(query)
                    }
                    .launchIn(viewModelScope)
            }
            NoteFindEvent.ClearSearchResult -> {
                _notes.postValue(emptyList())
                _nothing = false
                nothing.postValue(_nothing)
            }
        }
    }
}