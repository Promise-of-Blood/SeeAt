package com.pob.seeat.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

object ToggleLayoutAnimation {
    // LinearLayout
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

    // ConstraintLayout
    fun expand(view: ConstraintLayout) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        val constraintSet = ConstraintSet()
        constraintSet.clone(view)

        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animation = ValueAnimator.ofInt(0, targetHeight)
        animation.addUpdateListener { valueAnimator ->
            val layoutParams = view.layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            view.layoutParams = layoutParams
        }

        animation.duration =
            (targetHeight / view.context.resources.displayMetrics.density).toLong() * 2
        animation.start()
    }

    fun collapse(view: ConstraintLayout) {
        val initialHeight = view.height

        val animation = ValueAnimator.ofInt(initialHeight, 0)
        animation.addUpdateListener { valueAnimator ->
            val layoutParams = view.layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            view.layoutParams = layoutParams
        }
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })

        animation.duration =
            (initialHeight / view.context.resources.displayMetrics.density).toLong()
        animation.start()
    }
}