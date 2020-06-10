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

package com.materialstudies.reply.ui.compose

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Account
import com.materialstudies.reply.data.AccountStore
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.ComposeRecipientChipBinding
import com.materialstudies.reply.databinding.ComposeRecipientCardBinding
import com.materialstudies.reply.databinding.FragmentComposeBinding
import com.materialstudies.reply.util.themeColor
import com.materialstudies.reply.util.themeInterpolator
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A [Fragment] which allows for the composition of a new email.
 */
/* My Handler Methods */
interface ContainerTransformHandler {
    fun containerTransformChipToCard()
    fun containerTransformCardToChip()
}

class ComposeFragment : Fragment() {


    private val containerTranformHandler: ContainerTransformHandler = object : ContainerTransformHandler {
        override fun containerTransformCardToChip() {
            containerTransformRecipientCardToChip()
        }

        override fun containerTransformChipToCard() {
            containerTransformRecipientChipToCard()
        }
    }

    private lateinit var binding: FragmentComposeBinding

    private val args: ComposeFragmentArgs by navArgs()

    // The new email being composed.
    private val composeEmail: Email by lazy(NONE) {
        // Get the id of the email being replied to, if any, and either create an new empty email
        // or a new reply email.
        val id = args.replyToEmailId
        if (id == -1L) EmailStore.create() else EmailStore.createReplyTo(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComposeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.run {
            closeIcon.setOnClickListener { findNavController().navigateUp() }
            email = composeEmail

            composeEmail.nonUserAccountRecipients.forEach { addRecipientChip(it) }
            composeEmail.nonUserAccountRecipients.forEach{  addRecipientsCard(it) }

            binding.handlers = containerTranformHandler

            binding.recipientAddIcon.setOnClickListener {
                containerTransformRecipientChipToCard()
            }
            binding.recipientCardView.setOnClickListener {
                containerTransformRecipientChipToCard()
            }

            senderSpinner.adapter = ArrayAdapter(
                senderSpinner.context,
                R.layout.spinner_item_layout,
                AccountStore.getAllUserAccounts().map { it.email }
            )


            // Set transitions here so we are able to access Fragment's binding views.
            enterTransition = MaterialContainerTransform().apply {
                // Manually add the Views to be shared since this is not a standard Fragment to Fragment
                // shared element transition.
                startView = requireActivity().findViewById(R.id.fab)
                endView = emailCardView
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
                addTarget(R.id.email_card_view)
            }
        }
    }

    /**
     * Add a chip for the given [Account] to the recipients chip group.
     */
    private fun addRecipientChip(acnt: Account) {
        binding.recipientChipGroup.run {
            val chipBinding = ComposeRecipientChipBinding.inflate(
                LayoutInflater.from(context),
                this,
                false
            ).apply {
                account = acnt
            }
            addView(chipBinding.root)
        }
    }

    /**
     * Add a card for the given [Account] to the recipients card.
     */
    private fun addRecipientsCard(acnt: Account) {
        binding.recipientCardView.run {
            val cardBinding = ComposeRecipientCardBinding.inflate(
              LayoutInflater.from(context),
              this,
              false
            ).apply {
                account = acnt
            }

            addView(cardBinding.root)
        }
    }

    private fun prepareTransitions() {
        postponeEnterTransition()
    }

    private fun startTransitions() {
        binding.executePendingBindings()
        // Delay creating the enterTransition until after we have inflated this Fragment's binding
        // and are able to access the view to be transitioned to.
        enterTransition = MaterialContainerTransform().apply {
            // Manually add the Views to be shared since this is not a standard Fragment to Fragment
            // shared element transition.
            startView = requireActivity().findViewById(R.id.fab)
            endView = binding.emailCardView
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorOutgoing)
        }
        startPostponedEnterTransition()
    }

    private fun containerTransformRecipientChipToCard() {
        val transform = MaterialContainerTransform().apply{
            // Manually tell the container transform which Views to transform between.
            startView = binding.recipientChipGroup
            endView = binding.recipientCardView

            // Optionally add a curved path to the transform
            // pathMotion = MaterialArcMotion()

            // Since View to View transforms often are not transforming into full screens,
            // remove the transition's scrim.
            scrimColor = Color.TRANSPARENT

        }
        // Begin the transition by changing properties on the start and end views or
        // removing/adding them from the hierarchy.
        TransitionManager.beginDelayedTransition(binding.composeConstraintLayout, transform)
        binding.recipientCardView.visibility = View.VISIBLE
        binding.recipientChipGroup.visibility = View.GONE

    }

    private fun containerTransformRecipientCardToChip() {
        val transform = MaterialContainerTransform().apply{
            // Manually tell the container transform which Views to transform between.
            startView = binding.recipientCardView
            endView = binding.recipientChipGroup

            // Optionally add a curved path to the transform
            // pathMotion = MaterialArcMotion()

            // Since View to View transforms often are not transforming into full screens,
            // remove the transition's scrim.
            scrimColor = Color.TRANSPARENT

        }
        // Begin the transition by changing properties on the start and end views or
        // removing/adding them from the hierarchy.
        TransitionManager.beginDelayedTransition(binding.composeConstraintLayout, transform)
        binding.recipientCardView.visibility = View.GONE
        binding.recipientChipGroup.visibility = View.VISIBLE

    }
}