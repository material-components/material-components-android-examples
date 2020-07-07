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
import androidx.activity.OnBackPressedCallback
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
import com.materialstudies.reply.databinding.FragmentComposeBinding
import com.materialstudies.reply.util.themeColor
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A [Fragment] which allows for the composition of a new email.
 */
class ComposeFragment : Fragment() {

    private lateinit var binding: FragmentComposeBinding

    private val args: ComposeFragmentArgs by navArgs()

    // The new email being composed.
    private val composeEmail: Email by lazy(NONE) {
        // Get the id of the email being replied to, if any, and either create an new empty email
        // or a new reply email.
        val id = args.replyToEmailId
        if (id == -1L) EmailStore.create() else EmailStore.createReplyTo(id)
    }

    // Handle closing an expanded recipient card when on back is pressed.
    private val closeRecipientCardOnBackPressed = object : OnBackPressedCallback(false) {
        var expandedChip: View? = null
        override fun handleOnBackPressed() {
            expandedChip?.let { collapseChip(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeRecipientCardOnBackPressed)
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

            senderSpinner.adapter = ArrayAdapter(
                senderSpinner.context,
                R.layout.spinner_item_layout,
                AccountStore.getAllUserAccounts().map { it.email }
            )

            // Set transitions here so we are able to access Fragment's binding views.
            enterTransition = MaterialContainerTransform().apply {
                // Manually add the Views to be shared since this is not a standard Fragment to
                // Fragment shared element transition.
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
     *
     * This method also sets up the ability for expanding/collapsing the chip into a recipient
     * address selection dialog.
     */
    private fun addRecipientChip(acnt: Account) {
        binding.recipientChipGroup.run {
            val chipBinding = ComposeRecipientChipBinding.inflate(
                LayoutInflater.from(context),
                this,
                false
            ).apply {
                account = acnt
                root.setOnClickListener {
                    // Bind the views in the expanded card view to this account's details when
                    // clicked and expand.
                    binding.focusedRecipient = acnt
                    expandChip(it)
                }
            }
            addView(chipBinding.root)
        }
    }

    /**
     * Expand the recipient [chip] into a popup with a list of contact addresses to choose from.
     */
    private fun expandChip(chip: View) {
        // Configure the analogous collapse transform back to the recipient chip. This should
        // happen when the card is clicked, any region outside of the card (the card's transparent
        // scrim) is clicked, or when the back button is pressed.
        binding.run {
            recipientCardView.setOnClickListener { collapseChip(chip) }
            recipientCardScrim.visibility = View.VISIBLE
            recipientCardScrim.setOnClickListener { collapseChip(chip) }
        }
        closeRecipientCardOnBackPressed.expandedChip = chip
        closeRecipientCardOnBackPressed.isEnabled = true

        val transform = MaterialContainerTransform().apply {
            startView = chip
            endView = binding.recipientCardView
            scrimColor = Color.TRANSPARENT
            // Have the transform match the endView card's native elevation as closely as possible.
            endElevation = requireContext().resources.getDimension(
                R.dimen.email_recipient_card_popup_elevation_compat
            )
            // Avoid having this transform from running on both the start and end views by setting
            // its target to the endView.
            addTarget(binding.recipientCardView)
        }

        TransitionManager.beginDelayedTransition(binding.composeConstraintLayout, transform)
        binding.recipientCardView.visibility = View.VISIBLE
        // Using INVISIBLE instead of GONE ensures the chip's parent layout won't shift during
        // the transition due to chips being effectively removed.
        chip.visibility = View.INVISIBLE
    }

    /**
     * Collapse the recipient card back into its [chip] form.
     */
    private fun collapseChip(chip: View) {
        // Remove the scrim view and on back pressed callbacks
        binding.recipientCardScrim.visibility = View.GONE
        closeRecipientCardOnBackPressed.expandedChip = null
        closeRecipientCardOnBackPressed.isEnabled = false

        val transform = MaterialContainerTransform().apply {
            startView = binding.recipientCardView
            endView = chip
            scrimColor = Color.TRANSPARENT
            startElevation = requireContext().resources.getDimension(
                R.dimen.email_recipient_card_popup_elevation_compat
            )
            addTarget(chip)
        }

        TransitionManager.beginDelayedTransition(binding.composeConstraintLayout, transform)
        chip.visibility = View.VISIBLE
        binding.recipientCardView.visibility = View.INVISIBLE
    }
}