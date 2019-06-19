/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialstudies.owl.ui.featured

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.FragmentFeaturedBinding
import com.materialstudies.owl.model.courses

class FeaturedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeaturedBinding.inflate(inflater, container, false).apply {
            featuredGrid.apply {
                adapter = FeaturedAdapter().apply {
                    submitList(courses)
                }
                addItemDecoration(
                    OffsetDecoration(resources.getDimensionPixelSize(R.dimen.grid_0_5))
                )
            }
        }
        return binding.root
    }
}

class OffsetDecoration(@Px private val offset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(offset, offset, offset, offset)
    }
}
