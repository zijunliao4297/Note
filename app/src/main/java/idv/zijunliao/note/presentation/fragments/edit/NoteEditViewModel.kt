package idv.zijunliao.note.presentation.fragments.edit

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.zijunliao.note.Constants
import idv.zijunliao.note.R
import idv.zijunliao.note.data.use_case.NoteState
import idv.zijunliao.note.data.use_case.NoteUseCase
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.room.NoteInvalidException
import idv.zijunliao.note.data.store.DataStoreRepository
import idv.zijunliao.note.data.use_case.NoteEditEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(private val case: NoteUseCase, private val pref: DataStoreRepository) : ViewModel() {

    /**
     * must post value with a copy first
     * edits on the copy to avoid tampering on the original note
     * use two-way binding for xml data binding
     */
    val note = MutableLiveData<Note>()

    /**
     * note edit result for fragment presentation
     * use shared flow for once
     */
    val state = MutableSharedFlow<NoteState>()

    companion object {

        @JvmStatic
        @BindingAdapter("backgroundTint")
        fun setBackgroundTintAdapter(view: ImageView, color: Int) {
            view.backgroundTintList = ColorStateList.valueOf(color)
        }

        @JvmStatic
        @BindingAdapter("srcCompat")
        fun setSrcCompatAdapter(view: ImageView, id: Int) {
            if (id != -1) view.setImageResource(id) else view.setImageDrawable(null)
        }
    }

    /**
     * This method will be called by data-binding to get correspond tint color
     * @return Constants COLOR argb int to if equal to the editing note background
     */
    fun getColorTint(index: Int) = Constants.COLOR[index]

    /**
     * This method will be called by data-binding to set background color to editing note
     * @backgroundColor is a @Bindable value for room entity with two-way binding
     */
    fun setColorTint(index: Int) {
        note.value?.backgroundColor = getColorTint(index)
    }

    /**
     * Insert the edited note to database and emit the task result
     * @throws NoteInvalidException if note title or note content is blank
     */
    fun onEventHandle(event: NoteEditEvent) {
        when (event) {
            NoteEditEvent.AddNewNote -> {
                viewModelScope.launch {
                    try {
                        note.value?.apply {
                            timestamp = System.currentTimeMillis()
                            case.insertNote(this)
                            state.emit(NoteState.InsertSuccess)
                        }
                    } catch (e: NoteInvalidException) {
                        state.emit(NoteState.InsertInvalid(e.message))
                    }
                }
            }
            is NoteEditEvent.FirstOpen -> {
                viewModelScope.launch {
                    val titles = event.resources.getStringArray(R.array.tutorial_titles)
                    val contents = event.resources.getStringArray(R.array.tutorial_contents)
                    for (i in 0..2) {
                        case.insertNote(Note(
                            title = titles[i],
                            content = contents[i],
                            color = Constants.COLOR[i],
                            timestamp = System.currentTimeMillis()
                        ))
                        delay(1)
                    }
                    pref.updateOpenState()
                }
            }
            is NoteEditEvent.AddNote -> {
                viewModelScope.launch {
                    try {
                        case.insertNote(event.note)
                    } catch (e: NoteInvalidException) {
                        state.emit(NoteState.InsertInvalid(e.message))
                    }
                }
            }
        }
    }
}