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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.CourseItemBinding
import com.materialstudies.owl.util.ShapeAppearanceTransformation

class MyCourseViewHolder(
    private val binding: CourseItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(course: Course, imageTransform: ShapeAppearanceTransformation) {
        binding.course = course
        Glide.with(binding.courseImage)
            .load("https://source.unsplash.com/random/200x200")
            .transform(imageTransform)
            .into(binding.courseImage)
        Glide.with(binding.courseInstructor)
            .load("https://i.pravatar.cc/56")
            .circleCrop()
            .into(binding.courseInstructor)
        binding.executePendingBindings()
    }
}

class MyCoursesAdapter(private val lifecycle: LifecycleOwner) :
    ListAdapter<Course, MyCourseViewHolder>(CourseDiff) {

    private val shapeTransform =
        ShapeAppearanceTransformation(R.style.ShapeAppearance_Owl_SmallComponent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCourseViewHolder {
        val binding =
            CourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                lifecycleOwner = lifecycle
            }
        return MyCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCourseViewHolder, position: Int) {
        holder.bind(getItem(position), shapeTransform)
    }

    object CourseDiff : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Course, newItem: Course) = oldItem == newItem
    }
}
