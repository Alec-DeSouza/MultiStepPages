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

package com.umbraltech.multisteppages.testapp.flow

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.umbraltech.multisteppages.MultiStepDialogFragment
import com.umbraltech.multisteppages.Step
import com.umbraltech.multisteppages.StepAdvanceListener
import com.umbraltech.multisteppages.StepLayoutListener
import com.umbraltech.multisteppages.indicator.Indicator
import com.umbraltech.multisteppages.testapp.R
import com.umbraltech.multisteppages.testapp.dialog.LoadingDialogFragment
import kotlinx.android.synthetic.main.step_1_layout.view.*
import kotlinx.android.synthetic.main.step_2_layout.view.*
import kotlinx.android.synthetic.main.step_3_layout.view.*

class RegistrationFlow(
    private val indicator: Indicator,
    private val fragmentManager: FragmentManager
) {
    companion object {
        private val TAG: String? = RegistrationFlow::class.simpleName
    }

    private var firstName: String? = null
    private var lastName: String? = null

    private var username: String? = null
    private var password: String? = null

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment("Verifying. Please wait...")
    }

    private val firstStep: Step<Unit, Unit> = Step(
        title = "1. Personal Information",
        layoutRes = R.layout.step_1_layout,
        layoutListener = StepLayoutListener { view ->
            with(view) {
                step_1_first_name.addTextChangedListener {
                    step_1_first_name_layout.error = null
                }

                step_1_last_name.addTextChangedListener {
                    step_1_last_name_layout.error = null
                }
            }
        },
        advanceListener = StepAdvanceListener(
            onPreExecute = { view ->
                with(view) {
                    var terminate = false

                    if (step_1_first_name.text.isNullOrBlank()) {
                        step_1_first_name_layout.error = "First name cannot be blank"
                        terminate = true
                    }

                    if (step_1_last_name.text.isNullOrBlank()) {
                        step_1_last_name_layout.error = "Last name cannot be blank"
                        terminate = true
                    }

                    if (terminate) {
                        throw Exception("Terminating due to blank fields")
                    }
                }
            },
            onPostExecute = { view, _ ->
                with(view) {
                    firstName = step_1_first_name.text.toString()
                    lastName = step_1_last_name.text.toString()
                }

                true
            }
        )
    )

    private val secondStep: Step<Unit, Unit> = Step(
        title = "2. User Credentials",
        layoutRes = R.layout.step_2_layout,
        layoutListener = StepLayoutListener { view ->
            with(view) {
                step_2_username.addTextChangedListener {
                    step_2_username_layout.error = null
                }

                step_2_password.addTextChangedListener {
                    step_2_password_layout.error = null
                }

                step_2_repeat_password.addTextChangedListener {
                    step_2_repeat_password_layout.error = null
                }
            }
        },
        advanceListener = StepAdvanceListener(
            onPreExecute = { view ->
                with(view) {
                    var terminate = false

                    if (step_2_username.text.isNullOrBlank()) {
                        step_2_username_layout.error = "Username cannot be blank"
                        terminate = true
                    }

                    if (step_2_password.text.isNullOrBlank()) {
                        step_2_password_layout.error = "Password cannot be blank"
                        terminate = true
                    }

                    if (step_2_repeat_password.text.isNullOrBlank()) {
                        step_2_repeat_password_layout.error = "Repeat password cannot be blank"
                        terminate = true
                    } else {
                        if (step_2_password.text.toString() != step_2_repeat_password.text.toString()) {
                            step_2_repeat_password_layout.error = "Passwords do not match"
                            terminate = true
                        }
                    }

                    if (terminate) {
                        throw Exception("Terminating due to blank fields")
                    }

                    loadingDialog.show(fragmentManager)
                }
            },
            onExecute = {
                // Simulate slow network operation to verify username uniqueness
                Thread.sleep(3000)
            },
            onPostExecute = { view, _ ->
                with(view) {
                    loadingDialog.hide()

                    username = step_2_username.text.toString()
                    password = step_2_password.text.toString()
                }

                true
            }
        )
    )

    private val thirdStep: Step<Unit, Unit> = Step(
        title = "3. Summary",
        layoutRes = R.layout.step_3_layout,
        layoutListener = StepLayoutListener { view ->
            with(view) {
                step_3_full_name.text = "Name: $firstName $lastName"
                step_3_username.text = "Username: $username"
                step_3_password.text = "Password: $password"
            }
        },
        advanceListener = StepAdvanceListener()
    )

    fun execute() = MultiStepDialogFragment.create(
        "Registration",
        listOf(firstStep, secondStep, thirdStep),
        indicator
    ).show(fragmentManager, TAG)
}