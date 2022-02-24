package idv.zijunliao.note.presentation.widgets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import idv.zijunliao.note.presentation.fragments.list.NoteListViewModel
import idv.zijunliao.note.R
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.databinding.LayoutNoteItemBinding
import idv.zijunliao.note.presentation.fragments.list.NoteListFragmentDirections

class NoteListAdapter(private val listViewModel: NoteListViewModel) : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {
    private var mNotes = emptyList<Note>()

    /**
     * Consider to set background with MaterialShapeDrawable and CornerTreatment
     * Set dataBinding in other constructor when trying to add new layout types
     */
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var mLayoutNoteItemBinding: LayoutNoteItemBinding

        constructor(binding: LayoutNoteItemBinding) : this(binding.root) {
            mLayoutNoteItemBinding = binding
        }

        fun onBind(note: Note, listViewModel: NoteListViewModel) {
            mLayoutNoteItemBinding.note = note
            mLayoutNoteItemBinding.model = listViewModel
            mLayoutNoteItemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutNoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.itemView.setOnClickListener { view ->
            val extras = FragmentNavigatorExtras(holder.itemView to view.context.getString(R.string.transition_to_showFragment))
            val direction = NoteListFragmentDirections.actionNoteListFragmentToNoteShowFragment(mNotes[position])
            view.findNavController().navigate(direction, extras)
        }
        holder.onBind(mNotes[position], listViewModel)
    }

    override fun getItemCount(): Int = mNotes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotes(notes: List<Note>) {
        mNotes = notes
        notifyDataSetChanged()
    }
}