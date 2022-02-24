package idv.zijunliao.note.data.room

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import idv.zijunliao.note.BR
import idv.zijunliao.note.Constants.Blue300
import kotlinx.parcelize.Parcelize
import java.util.*
import idv.zijunliao.note.data.use_case.NoteEditEvent
import idv.zijunliao.note.data.use_case.NoteListEvent


@Parcelize
@Entity
data class Note(
    var title: String = "",
    var content: String = "",
    var timestamp: Long = 0L,
    var color: Int = Blue300,
    @PrimaryKey
    val id: Int? = null
) : BaseObservable(), Parcelable {

    var backgroundColor: Int
        @Bindable get() = color
        set(value) {
            color = value
            notifyPropertyChanged(BR.backgroundColor)
        }

    fun formatTime(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return formatter.format(calendar.time)
    }

    fun spannableTitle(query: String): SpannableString {
        val spannable = SpannableString(title)
        query.toRegex(RegexOption.IGNORE_CASE).findAll(title).map {
            spannable.setSpan(BackgroundColorSpan(Color.WHITE), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }.toList()
        return spannable
    }

    fun spannableContent(query: String): SpannableString {
        val spannable = SpannableString(content)
        query.toRegex(RegexOption.IGNORE_CASE).findAll(content).map {
            spannable.setSpan(BackgroundColorSpan(Color.WHITE), it.range.first, it.range.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }.toList()
        return spannable
    }
}

/**
 * Exception class to handle note events.
 * @see NoteRepository
 * @see NoteListEvent
 * @see NoteEditEvent
 */
class NoteInvalidException(message: String) : Exception(message)
