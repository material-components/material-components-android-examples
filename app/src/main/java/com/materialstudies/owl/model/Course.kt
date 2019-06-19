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

package com.materialstudies.owl.model

import androidx.recyclerview.widget.DiffUtil

data class Course(
    val id: Long,
    val name: String,
    val subject: String,
    val thumb: String,
    val thumbContentDesc: String,
    val steps: Int,
    val step: Int,
    val instructor: String
)

object CourseDiff : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(oldItem: Course, newItem: Course) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Course, newItem: Course) = oldItem == newItem
}

val courses = listOf(
    Course(
        id = 0,
        name = "Basic Blocks and Woodturning",
        subject = "Arts & Crafts",
        thumb = "",
        thumbContentDesc = "",
        steps = 7,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 1,
        name = "An Introduction To Oil Painting On Canvas",
        subject = "Painting",
        thumb = "",
        thumbContentDesc = "",
        steps = 12,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Understanding the Composition of Modern Cities",
        subject = "Architecture",
        thumb = "",
        thumbContentDesc = "",
        steps = 18,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Learning The Basics of Brand Identity",
        subject = "Design",
        thumb = "",
        thumbContentDesc = "",
        steps = 22,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Wooden Materials and Sculpting Machinery",
        subject = "Arts & Crafts",
        thumb = "",
        thumbContentDesc = "",
        steps = 19,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Advanced Potter's Wheel",
        subject = "Arts & Crafts",
        thumb = "",
        thumbContentDesc = "",
        steps = 14,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Advanced Abstract Shapes & 3D",
        subject = "Arts & Crafts",
        thumb = "",
        thumbContentDesc = "",
        steps = 17,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Beginning Portraiture",
        subject = "Photography",
        thumb = "",
        thumbContentDesc = "",
        steps = 22,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Intermediate Knife Skills",
        subject = "Culinary",
        thumb = "",
        thumbContentDesc = "",
        steps = 14,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Pattern Making for Begginers",
        subject = "Fashion",
        thumb = "",
        thumbContentDesc = "",
        steps = 7,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Location Lighting for Beginners",
        subject = "Photography",
        thumb = "",
        thumbContentDesc = "",
        steps = 6,
        step = 1,
        instructor = ""
    ),
    Course(
        id = 2,
        name = "Cinematography & Lighting",
        subject = "Film",
        thumb = "",
        thumbContentDesc = "",
        steps = 4,
        step = 1,
        instructor = ""
    )
)
