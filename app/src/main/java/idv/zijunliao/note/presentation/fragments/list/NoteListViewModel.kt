package idv.zijunliao.note.presentation.fragments.list

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.zijunliao.note.Constants.AppDebug
import idv.zijunliao.note.data.use_case.NoteListEvent
import idv.zijunliao.note.data.use_case.NoteUseCase
import idv.zijunliao.note.data.use_case.Order
import idv.zijunliao.note.data.use_case.Sort
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.store.DataStoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(private val case: NoteUseCase, private val pref: DataStoreRepository) : ViewModel() {

    /**
     * note list live date for ui presentation
     * initial value at the init function
     * observe result to update recycler view
     * @see NoteListFragment
     */
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    /**
     * a temporary note reference for restore function
     * always setup to null after restore worked
     */
    private var mTemporaryNote: Note? = null

    /**
     * job for get notes in coroutine
     * cancel it before getting notes to avoid multiple call
     */
    private var mSelectJob: Job? = null

    /**
     * sort reference for notes list
     * @see NoteListEvent setup by Resort event and Reorder event
     */
    private var mSort: Sort = Sort.Timestamp(Order.Decreasing)

    /**
     * reference of the last note event
     * for animation updatable checking
     */
    private var mLastNoteEvent: NoteListEvent? = null

    /**
     * mutable span count for reset StaggeredGridLayoutManager
     */
    var spanCount = pref.spanCountFlow // MutableStateFlow(1)

    /**
     * check is app first open to show the default tutorial notes
     */
    val firstOpen = pref.firstOpenFlow.asLiveData()

    /**
     * initialize notes list by query note database with sort strategy saved in the datastore
     * sort flow will collect the edited result every time
     */
    init {
        viewModelScope.launch {
            pref.sortOrderFlow.collect {
                getNotes(it)
            }
        }
    }

    /**
     * Method for handling on ui event
     * @see NoteListEvent shows what events to handle with
     */
    fun onEventHandle(event: NoteListEvent) {
        mLastNoteEvent = event
        when (event) {
            NoteListEvent.Restore -> {
                mTemporaryNote?.apply {
                    viewModelScope.launch {
                        case.insertNote(this@apply)
                    }
                }
                mTemporaryNote = null
            }
            is NoteListEvent.Reorder -> {
                viewModelScope.launch {
                    pref.updateOrderType(mSort.order.inverse())
                }
            }
            is NoteListEvent.Delete -> {
                viewModelScope.launch {
                    _notes.value?.get(event.index)?.let { note ->
                        mTemporaryNote = note
                        case.deleteNote(note)
                    }
                }
            }
            is NoteListEvent.Resort -> {
                if (event.sort.javaClass != mSort.javaClass) {
                    viewModelScope.launch {
                        pref.updateSortType(event.sort)
                    }
                }
            }
            is NoteListEvent.ChangeSpanCount -> {
                viewModelScope.launch {
                    pref.updateSpanCount(event.spanCount)
                }
            }
        }
    }

    /**
     * Method for query notes from database with the sorting operation
     * Must cancel old job reference before launch new one
     * reassign sort reference
     */
    private fun getNotes(sort: Sort) {
        mSelectJob?.cancel()
        mSort = sort
        mSelectJob = case.getNotes(sort)
            .onEach { list -> _notes.postValue(list) }
            .launchIn(viewModelScope)
    }

    /**
     * Method for reset order ui with current ordinal
     * @return current sort order
     */
    fun order() = mSort.order

    /**
     * Method for reset sort ui
     * @return current sort
     */
    fun sort() = mSort

    /**
     * Method for schedule recycler item layout animation
     * @return recycler item layout animation is updatable
     */
    fun updatable() =
        mLastNoteEvent !is NoteListEvent.Delete && mLastNoteEvent !is NoteListEvent.Restore
}