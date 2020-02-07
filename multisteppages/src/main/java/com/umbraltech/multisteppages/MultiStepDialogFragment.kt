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

package com.umbraltech.multisteppages

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.umbraltech.multisteppages.indicator.Indicator
import com.umbraltech.multisteppages.indicator.view.BarIndicatorView
import com.umbraltech.multisteppages.indicator.view.IndicatorView
import com.umbraltech.multisteppages.indicator.view.StepIndicatorView
import kotlinx.android.synthetic.main.multi_step_layout.*
import kotlinx.android.synthetic.main.multi_step_layout.view.*

class MultiStepDialogFragment : DialogFragment() {
    companion object {
        private val TAG: String? = MultiStepDialogFragment::class.simpleName

        fun create(title: String, steps: List<Step<*, *>>, indicator: Indicator = Indicator.NONE) =
            MultiStepDialogFragment().apply {
                retainInstance = true

                this.title = title
                this.steps = steps
                this.indicator = indicator
            }
    }

    private lateinit var title: String
    private lateinit var steps: List<Step<*, *>>
    private lateinit var indicator: Indicator

    private lateinit var dialog: AlertDialog

    private var indicatorView: IndicatorView? = null

    private val pagerAdapter: PageAdapter by lazy {
        PageAdapter(steps)
    }

    private val layoutListener: View.OnLayoutChangeListener =
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val position = dialog.multi_step_layout_view_pager.currentItem

            val view = pagerAdapter.getView(position)
            dialog.multi_step_layout_view_pager.updatePagerHeightForChild(view)
        }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        indicatorView = when (indicator) {
            Indicator.STEP -> StepIndicatorView(requireContext(), stepCount = steps.size)
            Indicator.BAR -> BarIndicatorView(requireContext())
            Indicator.NONE -> null
        }

        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.multi_step_layout,
            null,
            false
        ).apply {
            multi_step_layout_view_pager.apply {
                isUserInputEnabled = false
                adapter = pagerAdapter
            }

            indicatorView?.let {
                val layoutParams = ConstraintLayout.LayoutParams(
                    WRAP_CONTENT,
                    0
                ).apply {
                    leftToLeft = PARENT_ID
                    rightToRight = PARENT_ID
                    topToTop = R.id.multi_step_layout_back_button
                    bottomToBottom = R.id.multi_step_layout_back_button
                }

                multi_step_layout_root.addView(indicatorView, layoutParams)
            }
        }

        dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(view)
            .create()

        return dialog
    }

    override fun onStart() {
        super.onStart()

        with(dialog) {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

            multi_step_layout_next_button.setOnClickListener {
                dialogNextButtonPressed()
            }

            multi_step_layout_back_button.setOnClickListener {
                dialogBackButtonPressed()
            }

            multi_step_layout_view_pager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {

                    // Remove all pre-existing listeners
                    for (i: Int in 0 until pagerAdapter.itemCount) {
                        val view = pagerAdapter.getView(position)
                        view.removeOnLayoutChangeListener(layoutListener)
                    }

                    // Add listener for current page
                    val view = pagerAdapter.getView(position)
                    view.addOnLayoutChangeListener(layoutListener)

                    val title = steps[position].title ?: this@MultiStepDialogFragment.title

                    with(dialog) {
                        setTitle(title)

                        multi_step_layout_back_button.isEnabled = position > 0
                        multi_step_layout_next_button.setText(
                            if (position == (steps.size - 1)) R.string.finish else R.string.next
                        )

                        val stepView = pagerAdapter.getView(position)
                        steps[position].layoutListener?.onLayout?.invoke(stepView)
                    }

                    indicatorView?.setProgress(position.toFloat() / steps.size)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        pagerAdapter.saveState()
    }

    private fun dialogNextButtonPressed() {
        val curIndex = dialog.multi_step_layout_view_pager.currentItem

        steps[curIndex].advanceListener.execute(
            dialog,
            pagerAdapter.getView(curIndex),
            steps.size,
            lifecycleScope
        )
    }

    private fun dialogBackButtonPressed() {
        dialog.multi_step_layout_view_pager.currentItem--
    }

    // TODO: Fix visual artifact from initial resize
    private fun ViewPager2.updatePagerHeightForChild(view: View) = post {
        val wMeasureSpec = makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val hMeasureSpec = makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        view.measure(wMeasureSpec, hMeasureSpec)

        layoutParams = layoutParams.also { lp ->
            lp.height = view.measuredHeight
            lp.width = view.measuredWidth
        }
    }
}