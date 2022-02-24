package idv.zijunliao.note.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import idv.zijunliao.note.Constants
import idv.zijunliao.note.R
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.use_case.NoteEditEvent
import idv.zijunliao.note.presentation.fragments.edit.NoteEditViewModel
import idv.zijunliao.note.presentation.fragments.find.NoteFindViewModel
import idv.zijunliao.note.presentation.fragments.list.NoteListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mNoteEditViewModel by viewModels<NoteEditViewModel>()
    private val mNoteListViewModel by viewModels<NoteListViewModel>()
    private val mNoteFindViewModel by viewModels<NoteFindViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        mNoteListViewModel.firstOpen.observe(this) { first ->
            if (first) mNoteEditViewModel.onEventHandle(NoteEditEvent.FirstOpen(resources))
        }

        Log.d("AppDebug", System.currentTimeMillis().toString())
    }

    fun dummy() {
        lifecycleScope.launch {
            mNoteEditViewModel.onEventHandle(NoteEditEvent.AddNote(
                Note(
                    title = "test title",
                    content = "test content",
                    timestamp = System.currentTimeMillis(),
                    color = Constants.COLOR.random()
                )
            ))
            delay(10)
        }
    }
}