package com.lorenzl5.hermes.passwort.recycler

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

/**
 * Swipe-to-dismiss for RecyclerView items (left swipe reveals delete).
 */
class SwipeToDismissRecyclerView(
    context: android.content.Context
) : RecyclerView.ItemDecoration() {
}

class SwipeLayout(
    recyclerView: RecyclerView
) : RecyclerView.SimpleOnItemTouchListener() {

    var onSwipeDelete: ((com.lorenzl5.hermes.passwort.database.MetaData) -> Unit)? = null

    private val gestureDetector: GestureDetector
    private var touchSlop: Int
    private var activeView: View? = null
    private var initialX = 0f
    private var initialY = 0f

    init {
        touchSlop = ViewConfiguration.get(recyclerView.context).scaledTouchSlop
        gestureDetector = GestureDetector(recyclerView.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean = true
            })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y) ?: return false
        if (e.action == MotionEvent.ACTION_DOWN) {
            activeView = child
            initialX = e.x
            initialY = e.y
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        val view = activeView ?: return
        if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY
            if (Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) {
                // Start horizontal swipe
                if (dx < 0) {
                    // Swiping left
                    translateView(view, dx, rv)
                }
            }
        } else if (e.action == MotionEvent.ACTION_UP) {
            val dx = e.x - initialX
            if (dx < -view.width / 2f && activeView != null) {
                // Commit dismiss
                dismissView(view, rv)
            } else {
                // Reset
                view.translationX = 0f
            }
            activeView = null
        }
    }

    private fun translateView(view: View, dx: Float, rv: RecyclerView) {
        view.translationX = dx.coerceIn(-view.width.toFloat(), 0f)
    }

    private fun dismissView(view: View, rv: RecyclerView) {
        val position = rv.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return
        val item = (rv.adapter as? androidx.recyclerview.widget.RecyclerView.Adapter<*>)
            ?.getItemAt(position) as? com.lorenzl5.hermes.passwort.database.MetaData
        ?: return

        view.animate()
            .translationX(-view.width.toFloat())
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onSwipeDelete?.invoke(item)
                    view.translationX = 0f
                    view.alpha = 1f
                }
            })
            .start()
    }

    private fun RecyclerView.Adapter<*>?.getItemAt(position: Int): Any? {
        // Direct access via reflection or cast
        try {
            val method = this?.javaClass?.getMethod("getItem", Int::class.java)
            return method?.invoke(this, position)
        } catch (e: Exception) {
            return null
        }
    }
}
