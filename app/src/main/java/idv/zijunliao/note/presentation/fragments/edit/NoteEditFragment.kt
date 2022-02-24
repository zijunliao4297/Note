package idv.zijunliao.note.presentation.fragments.edit

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import idv.zijunliao.note.R
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.use_case.NoteEditEvent
import idv.zijunliao.note.data.use_case.NoteState
import idv.zijunliao.note.databinding.FragmentNoteEditBinding
import kotlinx.coroutines.launch

/**
 * Fragment to edit a note.
 * Create -> edit on a new empty note.
 * Modify -> edit on the copied note to prevent modify on origin data if edit were canceled.
 */
@AndroidEntryPoint
class NoteEditFragment : Fragment() {
    private val mNoteViewModel by activityViewModels<NoteEditViewModel>()
    private val mNavArgs by navArgs<NoteEditFragmentArgs>()
    private lateinit var mFragmentNoteEditBinding: FragmentNoteEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        mNoteViewModel.note.postValue(mNavArgs.note.copy())
        mFragmentNoteEditBinding = FragmentNoteEditBinding.inflate(inflater, container, false)
        mFragmentNoteEditBinding.model = mNoteViewModel
        mFragmentNoteEditBinding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch {
            mNoteViewModel.state.collect {
                mFragmentNoteEditBinding.root.clearFocus()
                when (it) {
                    is NoteState.InsertInvalid -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    NoteState.InsertSuccess -> findNavController().navigate(R.id.action_noteEditFragment_to_noteListFragment)
                }
            }
        }
        return mFragmentNoteEditBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_save) {
            mNoteViewModel.onEventHandle(NoteEditEvent.AddNewNote)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(note: Note) = NoteEditFragment()
    }
}