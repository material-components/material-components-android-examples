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

package com.materialstudies.reply.ui.email

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.android.material.transition.MaterialContainerTransform
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.data.EmailStore.updateOpenedEmailId
import com.materialstudies.reply.util.ReplyContentType
import com.materialstudies.reply.util.getContentType
import com.materialstudies.reply.util.themeColor
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A [Fragment] which displays a single, full email.
 */
class EmailFragment : Fragment() {

    private val args: EmailFragmentArgs by navArgs()
    private val emailId: Long by lazy(NONE) { args.emailId }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val email = EmailStore.get(emailId)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    val windowSize = calculateWindowSizeClass(this@EmailFragment.requireActivity())
                    val contentType = windowSize.getContentType()

                    LaunchedEffect(key1 = contentType) {
                        if (contentType == ReplyContentType.TWO_PANE) {
                            updateOpenedEmailId(emailId)
                            findNavController().navigateUp()
                        }
                    }

                    if (email != null)
                        EmailScreen(
                            email = email,
                            onBackClick = { findNavController().navigateUp() }
                        )
                }
            }
        }
    }
}