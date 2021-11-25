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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentEmailBinding
import com.materialstudies.reply.util.AdaptiveUtils
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.LARGE
import com.materialstudies.reply.util.EmailUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.XLARGE

private const val MAX_GRID_SPANS = 3

/**
 * A [Fragment] which displays a single, full email.
 */
class EmailFragment : Fragment() {

    private lateinit var binding: FragmentEmailBinding
    private var emailId: Long = 0
    private val attachmentAdapter = EmailAttachmentGridAdapter(MAX_GRID_SPANS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            EmailUtils.selectedEmailState.collect { id ->
                emailId = id
                binding.email = EmailStore.get(id)
            }
        }

        lifecycleScope.launch {
            AdaptiveUtils.screenSizeState.collect { size ->
                if (size == LARGE || size == XLARGE) {
                    binding.toolbar.navigationIcon = null
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val email = EmailStore.get(emailId)
        if (email == null) {
            showError()
            return
        }

        binding.run {
            this.email = email
            // Set up the staggered/masonry grid recycler
            attachmentRecyclerView.layoutManager = GridLayoutManager(
                requireContext(),
                MAX_GRID_SPANS
            ).apply {
                spanSizeLookup = attachmentAdapter.variableSpanSizeLookup
            }
            attachmentRecyclerView.adapter = attachmentAdapter
            attachmentAdapter.submitList(email.attachments)
        }
    }

    private fun showError() {
        // Do nothing
    }
}