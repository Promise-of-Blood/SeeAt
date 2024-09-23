package com.pob.seeat.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintLayout

object ToggleLayoutAnimation {
    fun expand(view: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                when (interpolatedTime.toInt() == 1) {
                    true -> view.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    false -> view.layoutParams.height = (targetHeight * interpolatedTime).toInt()
                }
                view.requestLayout()
            }

            override fun willChangeBounds() = true
        }

        animation.duration =
            (targetHeight / view.context.resources.displayMetrics.density).toLong() * 2
        view.startAnimation(animation)
    }

    fun collapse(view: View) {
        val initialHeight = view.measuredHeight
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                when (interpolatedTime.toInt() == 1) {
                    true -> view.visibility = View.GONE
                    false -> {
                        view.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        view.requestLayout()
                    }
                }
            }

            override fun willChangeBounds() = true
        }

        animation.duration =
            (initialHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
    }
}