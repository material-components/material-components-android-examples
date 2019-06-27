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

package com.materialstudies.reply.ui.nav

import android.view.View

/**
 * An action to be performed when a bottom sheet's slide offset is changed.
 *
 * 'slideOffset' will always be a float between the values of 0.0 and 1.0, 0.0 being hidden
 * and 1.0 being expanded.
 */
interface OnSlideAction {
    fun onSlide(sheet: View, slideOffset: Float)
}
