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

data class Topic(
    val name: String,
    val courses: Int,
    val imageUrl: String
)

object TopicDiff : DiffUtil.ItemCallback<Topic>() {
    override fun areItemsTheSame(oldItem: Topic, newItem: Topic) = oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: Topic, newItem: Topic) = oldItem == newItem
}

val topics = listOf(
    Topic("Architecture", 58, "https://source.unsplash.com/RFDP7_80v5A"),
    Topic("Arts & Crafts", 121, "https://source.unsplash.com/Tq4YjCa2BSc"),
    Topic("Business", 78, "https://source.unsplash.com/MYbhN8KaaEc"),
    Topic("Culinary", 118, "https://source.unsplash.com/uB7q7aipU2o"),
    Topic("Design", 423, "https://source.unsplash.com/cXkrqY2wFyc"),
    Topic("Fashion", 92, "https://source.unsplash.com/omgRZCmTvUM"),
    Topic("Film", 165, "https://source.unsplash.com/9lTUAlNB87M"),
    Topic("Gaming", 164, "https://source.unsplash.com/xITnxxlzGAE"),
    Topic("Illustration", 326, "https://source.unsplash.com/lxoq0zppH5w"),
    Topic("Lifestyle", 305, "https://source.unsplash.com/DzIt-fTYv4E"),
    Topic("Music", 212, "https://source.unsplash.com/LoppUA_9F1w"),
    Topic("Painting", 172, "https://source.unsplash.com/FwF_fKj5tBo"),
    Topic("Photography", 321, "https://source.unsplash.com/8nXKXYdO-Wk"),
    Topic("Technology", 118, "https://source.unsplash.com/MxVkWPiJALs")
)
