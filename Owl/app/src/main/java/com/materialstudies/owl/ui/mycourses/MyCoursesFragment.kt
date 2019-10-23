/*
 *   Copyright (c) 2019 Google Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the License
 *
 *   is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing permissions and limitations under
 *   the License.
 */
package com.materialstudies.owl.ui.mycourses

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.FragmentMyCoursesBinding
import com.materialstudies.owl.model.courses
import com.materialstudies.owl.util.SpringAddItemAnimator
import java.util.concurrent.TimeUnit

class MyCoursesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyCoursesBinding.inflate(inflater, container, false).apply {
            list.apply {
                itemAnimator = SpringAddItemAnimator()
                addItemDecoration(
                    BottomSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.grid_2))
                )
                adapter = MyCoursesAdapter().apply {
                    // add data after layout so that animations run
                    doOnNextLayout {
                        submitList(courses)
                        doOnNextLayout {
                            startPostponedEnterTransition()
                        }
                    }
                }
            }
        }
        postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
        return binding.root
    }
}

/**
 * A [RecyclerView.ItemDecoration] which adds the given `spacing` to the bottom of any view,
 * unless it is the last item.
 */
class BottomSpacingItemDecoration(
    @Px private val spacing: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lastItem = parent.getChildAdapterPosition(view) == state.itemCount - 1
        outRect.bottom = if (!lastItem) spacing else 0
    }
}
