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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.materialstudies.owl.databinding.FragmentFeaturedBinding
import com.materialstudies.owl.model.CourseId
import com.materialstudies.owl.model.courses
import com.materialstudies.owl.util.SpringAddItemAnimator
import java.util.concurrent.TimeUnit

/**
 * A [Fragment] displaying featured courses.
 */
class FeaturedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeaturedBinding.inflate(inflater, container, false).apply {
            val onClick: CourseViewClick = object : CourseViewClick {
                override fun onClick(view: View, courseId: CourseId) {
                    val extras = FragmentNavigatorExtras(
                        view to "shared_element"
                    )
                    val action = FeaturedFragmentDirections.actionFeaturedToLearn(courseId)
                    /* awaiting aosp/987385 in fragment 1.2.0-alpha02
                    exitTransition = Recede().apply {
                        addTarget(root)
                        excludeTarget(view, true)
                    duration = 300L
                    }*/
                    view.findNavController().navigate(action, extras)
                }
            }
            featuredGrid.apply {
                itemAnimator = SpringAddItemAnimator()
                adapter = FeaturedAdapter(onClick).apply {
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
