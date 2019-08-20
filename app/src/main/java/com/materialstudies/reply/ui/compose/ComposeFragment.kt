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

import android.os.Bundle
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Account
import com.materialstudies.reply.data.AccountStore
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.ComposeRecipientChipBinding
import com.materialstudies.reply.databinding.FragmentComposeBinding
import com.materialstudies.reply.util.FastOutUltraSlowIn
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide().apply {
            duration = resources.getInteger(R.integer.reply_motion_default_duration).toLong()
            interpolator = FastOutUltraSlowIn()
        }
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
}