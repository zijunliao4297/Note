package idv.zijunliao.note.presentation.fragments.list

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.OvershootInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import idv.zijunliao.note.R
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.data.use_case.NoteListEvent
import idv.zijunliao.note.data.use_case.NoteFindEvent
import idv.zijunliao.note.data.use_case.Order
import idv.zijunliao.note.data.use_case.Sort
import idv.zijunliao.note.databinding.FragmentNoteListBinding
import idv.zijunliao.note.presentation.fragments.find.NoteFindViewModel
import idv.zijunliao.note.presentation.widgets.NoteListAdapter
import idv.zijunliao.note.presentation.widgets.SpacesItemDecoration
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteListFragment : Fragment() {
    private val mNoteViewModel by activityViewModels<NoteListViewModel>()
    private val mNoteFindViewModel by activityViewModels<NoteFindViewModel>()
    private lateinit var mNoteAdapter: NoteListAdapter
    private val mSimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mNoteViewModel.onEventHandle(NoteListEvent.Delete(viewHolder.adapterPosition))
            Snackbar.make(viewHolder.itemView, R.string.snack_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.parseColor("#2979FF"))
                .setAction(R.string.snack_action) { mNoteViewModel.onEventHandle(NoteListEvent.Restore) }
                .show()
        }
    }
    private val mItemTouchHelper = ItemTouchHelper(mSimpleCallback)
    private lateinit var mFragmentNoteListBinding: FragmentNoteListBinding
    private var mSpanCount = 1
    private var mArrangeItem: MenuItem? = null
    private var mTriggerTime = 0L
        private set(value) {
            if (value - field > 500L) {
                field = value
                mNoteViewModel.onEventHandle(NoteListEvent.ChangeSpanCount(mSpanCount % 2 + 1))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        mFragmentNoteListBinding = FragmentNoteListBinding.inflate(inflater, container, false)
        mFragmentNoteListBinding.model = mNoteViewModel
        mFragmentNoteListBinding.lifecycleOwner = viewLifecycleOwner
        mNoteFindViewModel.onEventHandle(NoteFindEvent.ClearSearchResult)
        mNoteAdapter = NoteListAdapter(mNoteViewModel)
        return mFragmentNoteListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // view listeners
        mFragmentNoteListBinding.sort.setOnCheckedChangeListener { _, checkedId ->
            val order = mNoteViewModel.order()
            when (checkedId) {
                R.id.chipTitle -> mNoteViewModel.onEventHandle(NoteListEvent.Resort(Sort.Title(order)))
                R.id.chipTimestamp -> mNoteViewModel.onEventHandle(NoteListEvent.Resort(Sort.Timestamp(order)))
                R.id.chipColor -> mNoteViewModel.onEventHandle(NoteListEvent.Resort(Sort.Color(order)))
            }
        }
        mFragmentNoteListBinding.addNote.setOnClickListener {
            val extras = FragmentNavigatorExtras(it to getString(R.string.transition_to_editFragment))
            val direction = NoteListFragmentDirections.actionNoteListFragmentToNoteEditFragment(Note())
            findNavController().navigate(direction, extras)
        }
        mFragmentNoteListBinding.order.setTag(R.id.trigger, 0L)
        mFragmentNoteListBinding.order.setOnClickListener {
            (it.getTag(R.id.trigger) as? Long)?.apply {
                if (System.currentTimeMillis() - this > 500L) {
                    mNoteViewModel.onEventHandle(NoteListEvent.Reorder)
                    it.animate().rotationBy(180f).setDuration(400L).setInterpolator(OvershootInterpolator()).start()
                    it.setTag(R.id.trigger, System.currentTimeMillis())
                }
            }
        }
        mFragmentNoteListBinding.noteRecycler.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    recyclerView.invalidateItemDecorations()
                }
            })
            addItemDecoration(SpacesItemDecoration())
            mItemTouchHelper.attachToRecyclerView(this)
            adapter = mNoteAdapter
        }
        // observe parameters
        mNoteViewModel.notes.observe(viewLifecycleOwner) {
            // todo: optimize this code
            mFragmentNoteListBinding.sort.check(
                when (mNoteViewModel.sort()) {
                    is Sort.Color -> R.id.chipColor
                    is Sort.Timestamp -> R.id.chipTimestamp
                    is Sort.Title -> R.id.chipTitle
                }
            )
            mFragmentNoteListBinding.order.rotation = if (mNoteViewModel.order() == Order.Increasing) 180f else 0f
            mNoteAdapter.updateNotes(it)
            if (mNoteViewModel.updatable()) mFragmentNoteListBinding.noteRecycler.scheduleLayoutAnimation()
        }
        // post transition
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        // update ui state
        lifecycleScope.launch {
            mNoteViewModel.spanCount.collect {
                mSpanCount = it
                mFragmentNoteListBinding.noteRecycler.layoutManager = StaggeredGridLayoutManager(it, StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                }
                setArrangeIcon(mArrangeItem)
            }
        }
    }

    private fun setArrangeIcon(item: MenuItem?) {
        item?.setIcon(
            if (mSpanCount == 1) R.drawable.ic_baseline_dashboard_24
            else R.drawable.ic_baseline_table_rows_24
        )
    }

    /**
     * Assign the menu item reference
     * Set arrange icon at menu create. Because some device collect spanCount result too fast and the menu still null
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        mArrangeItem = menu.findItem(R.id.menu_arrange)
        setArrangeIcon(mArrangeItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> findNavController().navigate(R.id.action_noteListFragment_to_noteFindFragment)
            R.id.menu_arrange -> mTriggerTime = System.currentTimeMillis()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteListFragment()
    }
}