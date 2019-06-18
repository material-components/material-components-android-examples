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

data class Course(
    val id: Long,
    val name: String,
    val thumb: String,
    val thumbContentDesc: String,
    val steps: Int,
    val step: Int,
    val instructor: String
)

val courses = listOf(
    Course(
        id = 0,
        name = "Basic Blocks and Woodturning",
        thumb = "",
        thumbContentDesc = "",
        steps = 7,
        step = 1,
        instructor = ""),
    Course(
        id = 1,
        name = "Foos and Bars",
        thumb = "",
        thumbContentDesc = "",
        steps = 7,
        step = 1,
        instructor = ""),
    Course(
        id = 2,
        name = "Bazs and baxs",
        thumb = "",
        thumbContentDesc = "",
        steps = 7,
        step = 1,
        instructor = "")
)