package idv.zijunliao.note.presentation.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Extended the ChipGroup class to restrict chip children only shown in the max row.
 */
class ExtendedChipGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = com.google.android.material.R.attr.chipGroupStyle
) : ChipGroup(context, attrs, defStyleAttr) {
    private val maxRows = 2

    @SuppressLint("RestrictedApi")
    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(sizeChanged, left, top, right, bottom)
        var row = 0
        val end = right - left - paddingEnd
        var total = 0
        for (i in 0 until childCount)
            getChildAt(i).takeIf {
                it is Chip
            }?.apply {
                if (this.visibility == View.VISIBLE) {
                    total += (this.measuredWidth + chipSpacingHorizontal)
                    if (total >= end) {
                        row += 1
                        total = this.measuredWidth
                    }
                    if (row > maxRows) this.visibility = View.GONE
                }
            }
    }
}