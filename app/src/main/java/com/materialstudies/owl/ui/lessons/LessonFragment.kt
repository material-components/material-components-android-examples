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

package com.materialstudies.owl.ui.lessons

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.FragmentLessonBinding
import com.materialstudies.owl.model.lessons

/**
 * A [Fragment] displaying a lesson.
 */
class LessonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLessonBinding.inflate(inflater, container, false).apply {
            lesson = lessons.first()
            steps.apply {
                adapter = StepsAdapter(lessons, context)
            }
            collapse.setOnClickListener {
                it.findNavController().popBackStack()
            }
        }
        return binding.root
    }
}
