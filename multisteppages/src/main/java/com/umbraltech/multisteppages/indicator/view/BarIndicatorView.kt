/*
 * Copyright 2020 - present, Multi Step Pages contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.umbraltech.multisteppages.indicator.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.widget.ProgressBar
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class BarIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : IndicatorView(context, attrs, defStyleAttr) {
    companion object {
        private val TAG: String? = BarIndicatorView::class.simpleName
    }

    private val progressBar: ProgressBar =
        ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal)

    init {
        gravity = CENTER

        val layoutParams = LayoutParams(
            (100 * resources.displayMetrics.density + 0.5f).toInt(),
            LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = (25 * resources.displayMetrics.density + 0.5f).toInt()
            rightMargin = (25 * resources.displayMetrics.density + 0.5f).toInt()
        }

        addView(progressBar, layoutParams)
    }

    override fun setProgress(progress: Float) {
        ObjectAnimator.ofInt(
            progressBar,
            "progress",
            progressBar.progress,
            (progress * 100).toInt()
        ).apply {
            duration = 250
            interpolator = FastOutSlowInInterpolator()
        }.start()
    }
}