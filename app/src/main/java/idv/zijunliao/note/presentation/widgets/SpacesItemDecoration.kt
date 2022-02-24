package idv.zijunliao.note.presentation.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import idv.zijunliao.note.dp

/**
 * This class is design for StaggeredGridLayoutManager to set the correct item decoration on the ui.
 * Only handle two situation in this application.
 * @see StaggeredGridLayoutManager
 * @see RecyclerView.ItemDecoration
 */
class SpacesItemDecoration(private val space: Int = 8.dp) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        val count = (parent.layoutManager as StaggeredGridLayoutManager).spanCount
        if (count > 1)
            if (params.spanIndex % 2 == 0) {
                outRect.left = space
                outRect.right = space / 2
            } else {
                outRect.right = space
                outRect.left = space / 2
            }
        else {
            outRect.left = space
            outRect.right = space
        }
        outRect.bottom = space
    }
}