package idv.zijunliao.note.presentation.fragments.show

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import idv.zijunliao.note.R
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.databinding.FragmentNoteShowBinding
import idv.zijunliao.note.presentation.fragments.edit.NoteEditFragmentArgs
import idv.zijunliao.note.presentation.fragments.edit.NoteEditViewModel

/**
 * Fragment to show detail of a note
 * @See Note
 */
@AndroidEntryPoint
class NoteShowFragment : Fragment() {
    private val mNoteViewModel by activityViewModels<NoteEditViewModel>()
    private val mNavArgs by navArgs<NoteShowFragmentArgs>()
    private lateinit var mFragmentNoteShowBinding: FragmentNoteShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        mNoteViewModel.note.postValue(mNavArgs.note)
        mFragmentNoteShowBinding = FragmentNoteShowBinding.inflate(inflater, container, false)
        mFragmentNoteShowBinding.model = mNoteViewModel
        mFragmentNoteShowBinding.lifecycleOwner = viewLifecycleOwner
        return mFragmentNoteShowBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.show_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_edit) {
            findNavController().navigate(
                R.id.action_noteShowFragment_to_noteEditFragment,
                NoteEditFragmentArgs(mNavArgs.note).toBundle()
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(note: Note) = NoteShowFragment()
    }
}