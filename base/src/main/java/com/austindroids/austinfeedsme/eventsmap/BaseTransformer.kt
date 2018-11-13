package com.austindroids.austinfeedsme.eventsmap

import androidx.viewpager.widget.ViewPager.PageTransformer
import android.view.View


abstract class BaseTransformer : PageTransformer {

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected val isPagingEnabled: Boolean
        get() = false

    /**
     * Called each [.transformPage].
     *
     * @param view
     * @param position
     */
    protected abstract fun onTransform(view: View, position: Float)

    override fun transformPage(view: View, position: Float) {
        onPreTransform(view, position)
        onTransform(view, position)
        onPostTransform(view, position)
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * visibility of the fragment to [android.view.View.GONE]. Returning false will force the fragment to [android.view.View.VISIBLE].
     *
     * @return
     */
    protected fun hideOffscreenPages(): Boolean {
        return true
    }

    /**
     * Called each [.transformPage] before {[.onTransform] is called.
     *
     * @param view
     * @param position
     */
    protected fun onPreTransform(view: View, position: Float) {
        val width = view.getWidth()

        view.setRotationX(0f)
        view.setRotationY(0f)
        view.setRotation(0f)
        view.setScaleX(1f)
        view.setScaleY(1f)
        view.setPivotX(0f)
        view.setPivotY(0f)
        view.setTranslationY(0f)
        view.setTranslationX(if (isPagingEnabled) 0f else -width * position)

        if (hideOffscreenPages()) {
            view.setAlpha(if (position <= -1f || position >= 1f) 0f else 1f)
        } else {
            view.setAlpha(1f)
        }
    }

    /**
     * Called each [.transformPage] call after [.onTransform] is finished.
     *
     * @param view
     * @param position
     */
    protected fun onPostTransform(view: View, position: Float) {}

}