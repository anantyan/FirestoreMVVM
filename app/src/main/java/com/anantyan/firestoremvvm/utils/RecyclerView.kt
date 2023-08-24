package com.anantyan.firestoremvvm.utils

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.roundToInt

private val ATTRS = intArrayOf(R.attr.listDivider)
const val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
const val VERTICAL_LIST = LinearLayoutManager.VERTICAL

fun Context.onDecorationListener(
    orientation: Int,
    margin: Int
) = object : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable?
    private var mOrientation = 0

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == VERTICAL_LIST) drawVertical(c, parent)
        else drawHorizontal(c, parent)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == VERTICAL_LIST) outRect[0, 0, 0] = mDivider!!.intrinsicHeight
        else outRect[0, 0, mDivider!!.intrinsicWidth] = 0
    }

    private fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST)) { "Invalid orientation!" }
        mOrientation = orientation
    }

    private fun drawVertical(c: Canvas?, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider.setBounds(left + dpToPx(margin), top, right - dpToPx(margin), bottom)
            /*mDivider.setBounds(left + dpToPx(margin), top, right, bottom)*/
            mDivider.draw(c!!)
        }
    }

    private fun drawHorizontal(c: Canvas?, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mDivider!!.intrinsicHeight
            mDivider.setBounds(left, top + dpToPx(margin), right, bottom - dpToPx(margin))
            mDivider.draw(c!!)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = this@onDecorationListener.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).roundToInt()
    }

    init {
        val a = this@onDecorationListener.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
        setOrientation(orientation)
    }
}