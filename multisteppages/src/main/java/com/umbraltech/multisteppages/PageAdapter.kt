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

import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class PageAdapter(private val steps: List<Step<*, *>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TAG: String? = PageAdapter::class.simpleName
    }

    class StepViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    private val viewMap: MutableMap<Int, View> = mutableMapOf()
    private val viewStateMap: MutableMap<Int, SparseArray<Parcelable>> = mutableMapOf()

    fun saveState() {
        for (i: Int in steps.indices) {
            val viewState = SparseArray<Parcelable>()
            viewMap[i]?.saveHierarchyState(viewState)
            viewStateMap[i] = viewState
        }
    }

    fun getView(position: Int): View = viewMap[position]!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            steps[viewType].layoutRes,
            parent,
            false
        )

        // Keep track of existing views
        viewMap[viewType] = view

        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i(TAG, "Invoked onBindViewHolder($holder, $position)")

        // Only restore state if previously saved state exists
        if (viewStateMap.containsKey(position)) {
            holder.itemView.restoreHierarchyState(viewStateMap[position])
        }
    }

    override fun getItemCount(): Int = steps.size
    override fun getItemViewType(position: Int): Int = position
}