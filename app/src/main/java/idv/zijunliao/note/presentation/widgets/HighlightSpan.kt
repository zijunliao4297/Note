package idv.zijunliao.note.presentation.widgets

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import android.util.Log
import idv.zijunliao.note.Constants.AppDebug
import idv.zijunliao.note.dp
import kotlin.math.roundToInt

@Deprecated("text size error")
class HighlightSpan(private val backgroundColor: Int, private val mPadding: Int = 1.dp) : ReplacementSpan() {
    private val mTextRect = RectF()

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int =
        (paint.measureText(text, start, end)).roundToInt()

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        Log.d(AppDebug, "$text, $start, $end, ${paint.measureText(text, start, end)}")
        // draw background
        mTextRect.set(x - mPadding, top.toFloat(), x + paint.measureText(text, start, end) + mPadding, bottom.toFloat())
        paint.color = backgroundColor
        canvas.drawRoundRect(mTextRect, 1f, 1f, paint)
        // draw text
        paint.color = Color.BLACK
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}