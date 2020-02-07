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

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.umbraltech.multisteppages.R

class StepIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private var stepCount: Int = 1
) : IndicatorView(context, attrs, defStyleAttr) {
    companion object {
        private val TAG: String? = StepIndicatorView::class.simpleName
    }

    private var currentStep: Int = 0

    private val stepSize: Int = (5.5 * resources.displayMetrics.density + 0.5f).toInt()
    private val stepMargin: Int = (3.5 * resources.displayMetrics.density + 0.5f).toInt()

    private var highlightColor: Int

    init {
        orientation = HORIZONTAL
        gravity = CENTER

        context.theme.obtainStyledAttributes(attrs, R.styleable.StepIndicatorView, 0, 0).apply {
            try {
                stepCount = getInt(R.styleable.StepIndicatorView_steps, stepCount)
            } finally {
                recycle()
            }
        }

        val typedValue = TypedValue()
        val textSizeAttr = intArrayOf(R.attr.colorAccent)

        context.theme.obtainStyledAttributes(typedValue.data, textSizeAttr).apply {
            try {
                highlightColor = getColor(0, resources.getColor(R.color.stepHighlight))
            } finally {
                recycle()
            }
        }

        for (i in 0 until stepCount) {
            val view = ImageView(context).apply {
                setImageDrawable(resources.getDrawable(R.drawable.circle).mutate())

                if (i == 0) {
                    DrawableCompat.setTint(drawable, highlightColor)
                }
            }

            val layoutParams = LayoutParams(stepSize, stepSize).apply {
                leftMargin = stepMargin
                rightMargin = stepMargin
            }

            addView(view, layoutParams)
        }
    }

    override fun setProgress(progress: Float) {
        val updatedStep = (stepCount * progress).toInt()

        val currentImageView = getChildAt(currentStep) as ImageView
        DrawableCompat.setTint(currentImageView.drawable, resources.getColor(R.color.stepDefault))

        val updatedImageView = getChildAt(updatedStep) as ImageView
        DrawableCompat.setTint(updatedImageView.drawable, highlightColor)

        currentStep = updatedStep
    }
}