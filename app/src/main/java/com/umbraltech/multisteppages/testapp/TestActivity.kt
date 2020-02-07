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

package com.umbraltech.multisteppages.testapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umbraltech.multisteppages.MultiStepDialogFragment
import com.umbraltech.multisteppages.Step
import com.umbraltech.multisteppages.StepAdvanceListener
import com.umbraltech.multisteppages.StepLayoutListener
import com.umbraltech.multisteppages.indicator.Indicator
import com.umbraltech.multisteppages.testapp.flow.RegistrationFlow
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.step_1_layout.view.*
import kotlinx.android.synthetic.main.step_3_layout.view.*

class TestActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = TestActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        test_activity_button_dialog_step.setOnClickListener {
            RegistrationFlow(Indicator.STEP, supportFragmentManager).execute()
        }

        test_activity_button_dialog_progress.setOnClickListener {
            RegistrationFlow(Indicator.BAR, supportFragmentManager).execute()
        }

        test_activity_button_dialog_none.setOnClickListener {
            RegistrationFlow(Indicator.NONE, supportFragmentManager).execute()
        }
    }
}
