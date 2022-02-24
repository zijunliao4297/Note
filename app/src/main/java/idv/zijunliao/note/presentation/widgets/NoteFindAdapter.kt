package idv.zijunliao.note.presentation.widgets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import idv.zijunliao.note.R
import idv.zijunliao.note.data.room.Note
import idv.zijunliao.note.databinding.LayoutNoteHintBinding
import idv.zijunliao.note.presentation.fragments.find.NoteFindFragmentDirections
import idv.zijunliao.note.presentation.fragments.find.NoteFindViewModel

class NoteFindAdapter(private val findViewModel: NoteFindViewModel) : RecyclerView.Adapter<NoteFindAdapter.NoteViewHolder>() {
    private var mNotes = emptyList<Note>()

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var mLayoutNoteHintBinding: LayoutNoteHintBinding

        constructor(binding: LayoutNoteHintBinding) : this(binding.root) {
            mLayoutNoteHintBinding = binding
        }

        fun onBind(note: Note, model: NoteFindViewModel) {
            mLayoutNoteHintBinding.note = note
            mLayoutNoteHintBinding.model = model
            mLayoutNoteHintBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutNoteHintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.itemView.setOnClickListener { view ->
            val extras = FragmentNavigatorExtras(holder.itemView to view.context.getString(R.string.transition_to_showFragment))
            val direction = NoteFindFragmentDirections.actionNoteFindFragmentToNoteShowFragment(mNotes[position])
            view.findNavController().navigate(direction, extras)
        }
        holder.onBind(mNotes[position], findViewModel)
    }

    override fun getItemCount(): Int = mNotes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotes(notes: List<Note>) {
        mNotes = notes
        notifyDataSetChanged()
    }
}