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

package com.materialstudies.reply.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.google.accompanist.themeadapter.material.MdcTheme
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.MainActivity
import com.materialstudies.reply.ui.MenuBottomSheetDialogFragment
import com.materialstudies.reply.ui.nav.NavigationModel
import com.materialstudies.reply.util.getContentType

/**
 * A [Fragment] that displays a list of emails.
 */
class HomeFragment : Fragment() {

    private val args: HomeFragmentArgs by navArgs()

    // An on back pressed callback that handles replacing any non-Inbox HomeFragment with inbox
    // on back pressed.
    private val nonInboxOnBackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            NavigationModel.setNavigationMenuItemChecked(NavigationModel.INBOX_ID)
            (requireActivity() as MainActivity)
                .navigateToHome(R.string.navigation_inbox, Mailbox.INBOX);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    val windowSize = calculateWindowSizeClass(this@HomeFragment.requireActivity())
                    val displayFeatures =
                        calculateDisplayFeatures(this@HomeFragment.requireActivity())
                    val contentType = windowSize.getContentType()

                    val emails =
                        EmailStore.getEmails(args.mailbox).observeAsState().value ?: emptyList()
                    val openedEmailId = EmailStore.openedEmailId.observeAsState().value ?: 0
                    val email = EmailStore.get(openedEmailId)

                    HomeScreen(
                        email = email,
                        emails = emails,
                        onNavigateToEmail = { emailId -> onEmailClick(emailId) },
                        onOpenEmail = { emailId -> EmailStore.updateOpenedEmailId(emailId) },
                        onEmailLongClick = { onEmailLongClick() },
                        contentType = contentType,
                        displayFeatures = displayFeatures
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Postpone enter transitions to allow shared element transitions to run.
        // https://github.com/googlesamples/android-architecture-components/issues/495
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        // Only enable the on back callback if this home fragment is a mailbox other than Inbox.
        // This is to make sure we always navigate back to Inbox before exiting the app.
        nonInboxOnBackCallback.isEnabled = args.mailbox != Mailbox.INBOX
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            nonInboxOnBackCallback
        )
    }

    private fun onEmailClick(emailId: Long) {
        // Set exit and reenter transitions here as opposed to in onCreate because these transitions
        // will be set and overwritten on HomeFragment for other navigation actions.
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        val directions = HomeFragmentDirections.actionHomeFragmentToEmailFragment(emailId)
        findNavController().navigate(directions)
    }

    private fun onEmailLongClick(): Boolean {
        MenuBottomSheetDialogFragment
            .newInstance(R.menu.email_bottom_sheet_menu)
            .show(parentFragmentManager, null)
        return true
    }

    /** TODO: Implement the Starring function
     * @see com.materialstudies.reply.ui.home.ReboundingSwipeActionCallback
     */
    private fun onEmailStarChanged(email: Email, newValue: Boolean) {
        EmailStore.update(email.id) { isStarred = newValue }
    }
}
