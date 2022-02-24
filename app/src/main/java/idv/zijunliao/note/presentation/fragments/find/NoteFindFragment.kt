package idv.zijunliao.note.presentation.fragments.find

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialContainerTransform
import idv.zijunliao.note.R
import idv.zijunliao.note.data.use_case.NoteFindEvent
import idv.zijunliao.note.databinding.FragmentNoteFindBinding
import idv.zijunliao.note.onQueryTextChanged
import idv.zijunliao.note.presentation.widgets.NoteFindAdapter
import idv.zijunliao.note.presentation.widgets.SpacesItemDecoration

class NoteFindFragment : Fragment() {
    private var mSearchView: SearchView? = null
    private val mNoteViewModel by activityViewModels<NoteFindViewModel>()
    private lateinit var mFragmentNoteFindBinding: FragmentNoteFindBinding
    private lateinit var mFindAdapter: NoteFindAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mFragmentNoteFindBinding = FragmentNoteFindBinding.inflate(inflater, container, false)
        mFragmentNoteFindBinding.model = mNoteViewModel
        mFragmentNoteFindBinding.fragment = this
        mFragmentNoteFindBinding.lifecycleOwner = viewLifecycleOwner
        mFindAdapter = NoteFindAdapter(mNoteViewModel)
        return mFragmentNoteFindBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentNoteFindBinding.chipGroupSuggestions.setOnCheckedChangeListener { group, checkedId ->
            group.findViewById<Chip>(checkedId)?.apply {
                mSearchView?.clearFocus()
                mSearchView?.setQuery(text, false)
                handleQuery(text.toString())
            }
        }
        mNoteViewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            mFragmentNoteFindBinding.chipGroupSuggestions.apply {
                removeAllViews()
                suggestions?.forEach {
                    val chip = layoutInflater.inflate(R.layout.layout_chip_item, this, false) as Chip
                    chip.id = View.generateViewId()
                    chip.text = it
                    addView(chip)
                }
            }
        }
        mFragmentNoteFindBinding.searchRecycler.apply {
            addItemDecoration(SpacesItemDecoration())
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
            adapter = mFindAdapter
        }
        mNoteViewModel.notes.observe(viewLifecycleOwner) { notes ->
            mFindAdapter.updateNotes(notes)
            mFragmentNoteFindBinding.searchRecycler.scheduleLayoutAnimation()
            mSearchView?.clearFocus()
        }
        mNoteViewModel.nothing.observe(viewLifecycleOwner) {
            val transform = MaterialContainerTransform().apply {
                if (it) {
                    startView = mFragmentNoteFindBinding.searchRecycler
                    endView = mFragmentNoteFindBinding.nothing
                } else {
                    startView = mFragmentNoteFindBinding.nothing
                    endView = mFragmentNoteFindBinding.searchRecycler
                }
                scrimColor = Color.TRANSPARENT
                duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                addTarget(if (it) mFragmentNoteFindBinding.nothing else mFragmentNoteFindBinding.searchRecycler)
            }
            TransitionManager.beginDelayedTransition(mFragmentNoteFindBinding.constraintLayout, transform)
        }
        // post transition
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    fun clearSuggestions() {
        mNoteViewModel.onEventHandle(NoteFindEvent.ClearSuggestion)
    }

    private fun handleQuery(query: String) {
        mNoteViewModel.onEventHandle(NoteFindEvent.Search(query))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val manager = context?.getSystemService(Context.SEARCH_SERVICE) as? SearchManager
        menu.findItem(R.id.search).apply {
            (actionView as? SearchView)?.apply {
                mSearchView = this
                activity?.apply {
                    windowManager?.apply {
                        maxWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) currentWindowMetrics.bounds.width() else defaultDisplay.width
                    }
                    setSearchableInfo(manager?.getSearchableInfo(componentName))
                }
                findViewById<EditText>(R.id.search_src_text).filters += InputFilter.LengthFilter(12)
                onQueryTextChanged { query -> handleQuery(query) }
            }
            expandActionView()
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                /**
                 * @return Always return false to avoid toolbar show up again
                 */
                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    findNavController().navigateUp()
                    return false
                }
            })
        }
        if (mNoteViewModel.notes.value?.isNotEmpty() == true)
            mSearchView?.apply {
                clearFocus()
                setQuery(mNoteViewModel.queryString.value ?: "", false)
            }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteFindFragment()
    }
}