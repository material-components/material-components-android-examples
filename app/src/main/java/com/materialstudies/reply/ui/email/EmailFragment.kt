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

import android.animation.AnimatorInflater
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.materialstudies.reply.App
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentEmailBinding
import com.materialstudies.reply.util.FastOutUltraSlowIn
import kotlin.LazyThreadSafetyMode.NONE

private const val MAX_GRID_SPANS = 3

/**
 * A [Fragment] which displays a single, full email.
 */
class EmailFragment : Fragment() {

    private lateinit var binding: FragmentEmailBinding

    private val emailId: Int by lazy(NONE) { navArgs<EmailFragmentArgs>().value.emailId }
    private lateinit var emailStore: EmailStore

    private val attachmentAdapter = EmailAttachmentGridAdapter(MAX_GRID_SPANS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailStore = (requireActivity().application as App).emailStore

        binding.navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        val email = emailStore.get(emailId)
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

        startTransitions()
    }

    private fun prepareTransitions() {
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.email_card_shared_element_transition).apply {
                interpolator = FastOutUltraSlowIn()
            }
    }

    private fun startTransitions() {
        binding.executePendingBindings()
        startPostponedEnterTransition()
        AnimatorInflater.loadAnimator(requireContext(), R.animator.alpha_in).apply {
            setTarget(binding.emailCardView)
            start()
        }
    }

    private fun showError() {
        // Do nothing
    }
}