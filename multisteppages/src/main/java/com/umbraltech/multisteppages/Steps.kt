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

import android.app.Dialog
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.android.synthetic.main.multi_step_layout.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Step<PreExecuteResult, ExecuteResult>(
    val title: String? = null,
    @LayoutRes val layoutRes: Int,
    val layoutListener: StepLayoutListener? = null,
    val advanceListener: StepAdvanceListener<PreExecuteResult, ExecuteResult>
)

class StepLayoutListener(val onLayout: (View) -> Unit)

class StepAdvanceListener<PreExecuteResult, ExecuteResult>(
    private val onPreExecute: (View) -> PreExecuteResult? = { _ -> null },
    private val onExecute: (PreExecuteResult?) -> ExecuteResult? = { _ -> null },
    private val onPostExecute: (View, ExecuteResult?) -> Boolean = { _, _ -> true },
    private val onError: (View, Throwable) -> Unit = { _, _ -> }
) {
    companion object {
        private val TAG: String? = StepAdvanceListener::class.simpleName
    }

    internal fun execute(
        dialog: Dialog,
        stepView: View,
        totalStepCount: Int,
        lifecycleScope: LifecycleCoroutineScope
    ) {
        val errorHandler = CoroutineExceptionHandler { _, error ->
            lifecycleScope.launch(Dispatchers.Main) {
                onError(stepView, error)
            }
        }

        lifecycleScope.launch(errorHandler) {
            val preExecuteResult = withContext(Dispatchers.Main) {
                onPreExecute(stepView)
            }

            val executeResult = withContext(Dispatchers.IO) {
                onExecute(preExecuteResult)
            }

            withContext(Dispatchers.Main) {
                val advance = onPostExecute(stepView, executeResult)

                if (advance) {
                    val curIndex = dialog.multi_step_layout_view_pager.currentItem

                    if (curIndex == (totalStepCount - 1)) {
                        dialog.dismiss()
                    } else {
                        dialog.multi_step_layout_view_pager.currentItem = curIndex + 1
                    }
                }
            }
        }
    }
}
