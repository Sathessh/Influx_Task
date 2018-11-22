package satheesh.influxtask.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation


object AnimUtil {

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }


    fun getViewFlagWidth(mViewFlag: View): Int {
        var viewWidth = mViewFlag.width
        if (viewWidth == 0) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            mViewFlag.measure(widthSpec, heightSpec)
            viewWidth = mViewFlag.measuredWidth
        }
        return viewWidth
    }

    fun xAxismoveAnim(v: View, startX: Int, toX: Int) {
        moveAnim(v, startX, toX, 0, 0, 200)
    }

    private fun moveAnim(v: View, startX: Int, toX: Int, startY: Int, toY: Int, during: Long) {
        val anim = TranslateAnimation(startX.toFloat(), toX.toFloat(), startY.toFloat(), toY.toFloat())
        anim.duration = during
        anim.fillAfter = true
        v.startAnimation(anim)
    }

    fun closeExpandView(contentView: View, mViewFlag: View) {
        val isVisible = contentView.visibility == View.VISIBLE
        if (isVisible) {
            animateCollapsing(contentView, mViewFlag)
        }
    }

    fun animateCollapsing(view: View, mViewFlag: View) {
        val origHeight = view.height

        val animator = createHeightAnimator(view, origHeight, 0)
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animator: Animator) {
                view.visibility = View.GONE
                mViewFlag.clearAnimation()
                mViewFlag.visibility = View.GONE
            }
        })
        animator.start()
    }

    fun animateExpanding(view: View) {
        view.visibility = View.VISIBLE
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)
        Log.e("parent", "" + 0 + "--" + view.measuredHeight)
        val animator = createHeightAnimator(view, 0, view.measuredHeight)
        animator.start()
    }

    fun animateChildExpanding(parentView: View, childView: View) {

        val widthSpec1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        parentView.measure(widthSpec1, heightSpec1)

        val parentheight = parentView.measuredHeight

        childView.visibility = View.VISIBLE
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        childView.measure(widthSpec, heightSpec)

        Log.e("child", "" + parentheight + "--" + (childView.measuredHeight + parentheight))
        val animator = createHeightAnimator(parentView, parentheight, (childView.measuredHeight + parentheight))
        animator.start()
    }


    fun closeChildExpandView(parentView: View, childView: View) {
        val isVisible = childView.visibility == View.VISIBLE
        if (isVisible) {
            animateChildCollapsing(parentView, childView)
        }
    }

    fun animateChildCollapsing(parentView: View, childView: View) {
        val origHeight = childView.height

        Log.e("childCollaps", "" + parentView.height + "--" + origHeight)
        val animator = createHeightAnimator(parentView, parentView.height, origHeight)
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animator: Animator) {
                childView.visibility = View.GONE
            }
        })
        animator.start()
    }


    fun createHeightAnimator(view: View, start: Int, end: Int): ValueAnimator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        return animator
    }

}