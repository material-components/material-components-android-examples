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

data class Lesson(
    val title: String,
    val formattedSteps: String,
    val length: String,
    val thumbUrl: String,
    val thumbContentDescription: String = ""
)

object LessonDiff : DiffUtil.ItemCallback<Lesson>() {
    override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson) = oldItem.title == newItem.title
    override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson) = oldItem == newItem
}

val lessons = listOf(
    Lesson(
        title = "An introduction to the Landscape",
        formattedSteps = "01",
        length = "4:14",
        thumbUrl = "https://source.unsplash.com/NRQV-hBF10M"
    ),
    Lesson(
        title = "Movement and Expression",
        formattedSteps = "02",
        length = "7:28",
        thumbUrl = "https://source.unsplash.com/JhqhGfX_Wd8"
    ),
    Lesson(
        title = "Composition and the Urban Canvas",
        formattedSteps = "03",
        length = "3:43",
        thumbUrl = "https://source.unsplash.com/0OjzOqlJyoU"
    ),
    Lesson(
        title = "Lighting Techniques and Aesthetics",
        formattedSteps = "04",
        length = "4:45",
        thumbUrl = "https://source.unsplash.com/J5-Kqu_fxyo"
    ),
    Lesson(
        title = "Special Effects",
        formattedSteps = "05",
        length = "6:19",
        thumbUrl = "https://source.unsplash.com/9ZCZoH69dZQ"
    ),
    Lesson(
        title = "Techniques with Structures",
        formattedSteps = "06",
        length = "9:41",
        thumbUrl = "https://source.unsplash.com/RFDP7_80v5A"
    ),
    Lesson(
        title = "Deep Focus Using a Camera Dolly",
        formattedSteps = "07",
        length = "4:43",
        thumbUrl = "https://source.unsplash.com/0rZ2-QWtkwY"
    ),
    Lesson(
        title = "Point of View Shots with Structures",
        formattedSteps = "08",
        length = "9:41",
        thumbUrl = "https://source.unsplash.com/iQnR_xEsBj0"
    ),
    Lesson(
        title = "Photojournalism: Street Art",
        formattedSteps = "09",
        length = "9:41",
        thumbUrl = "https://source.unsplash.com/qX9Ie7ieb1E"
    )
)
