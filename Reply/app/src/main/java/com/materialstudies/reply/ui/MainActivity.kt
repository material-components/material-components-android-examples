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

package com.materialstudies.reply.ui

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.materialstudies.reply.R
import com.materialstudies.reply.databinding.ActivityMainBinding
import com.materialstudies.reply.ui.compose.ComposeFragmentDirections
import com.materialstudies.reply.ui.email.EmailFragmentArgs
import com.materialstudies.reply.ui.home.HomeFragmentDirections
import com.materialstudies.reply.ui.home.Mailbox
import com.materialstudies.reply.ui.nav.NavigationAdapter
import com.materialstudies.reply.ui.nav.NavigationModelItem
import com.materialstudies.reply.ui.search.SearchFragmentDirections
import com.materialstudies.reply.util.AdaptiveUtils
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.SMALL
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.MEDIUM
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.LARGE
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.XLARGE
import com.materialstudies.reply.util.contentView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
                     NavController.OnDestinationChangedListener,
                     NavigationAdapter.NavigationAdapterListener {

    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    // Keep track of the current Email being viewed, if any, in order to pass the correct email id
    // to ComposeFragment when this Activity's FAB is clicked.
    private var currentEmailId = -1L

    private var suspendNavigationItemChangedUpdates = false

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.childFragmentManager
                ?.fragments
                ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyIfAvailable(this)
        // Update the windows background color to be an elevated surface
        val surfaceColor5 = SurfaceColors.SURFACE_5.getColor(this)
        window.decorView.setBackgroundColor(surfaceColor5)
        super.onCreate(savedInstanceState)

        setUpNavigationComponentry()

        AdaptiveUtils.updateScreenSize(this)
        lifecycleScope.launch {
            AdaptiveUtils.screenSizeState.collect {
                when (it) {
                    SMALL -> adaptToSmallScreen()
                    MEDIUM -> adaptToMediumAndLargeScreen()
                    LARGE -> adaptToMediumAndLargeScreen()
                    XLARGE -> adaptToXLargeScreen()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AdaptiveUtils.updateScreenSize(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // Set the currentEmail being viewed so when the FAB is pressed, the correct email
        // reply is created. In a real app, this should be done in a ViewModel but is done
        // here to keep things simple. Here we're also setting the configuration of the
        // BottomAppBar and FAB based on the current destination.
        currentEmailId = when (destination.id) {
            R.id.emailFragment ->
                if (arguments == null) -1 else EmailFragmentArgs.fromBundle(arguments).emailId
            else -> -1
        }
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        // Swap the list of emails for the given mailbox
        navigateToHome(item.mailbox)
    }

    override fun onNavEmailFolderClicked(folder: NavigationModelItem.NavEmailFolder) {
        // Do nothing
    }

    fun closeEmailDetailsPane() {
        binding.slidingPaneLayout.closePane()
    }

    fun openEmailDetailsPane() {
        binding.slidingPaneLayout.openPane()
    }

    fun navigateToHome(mailbox: Mailbox) {
        currentNavigationFragment?.apply {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
        }
        val directions = HomeFragmentDirections.actionGlobalHomeFragment(mailbox)
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    fun navigateToSearch() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
        }
        val directions = SearchFragmentDirections.actionGlobalSearchFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToCompose() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
        }
        val directions = ComposeFragmentDirections.actionGlobalComposeFragment(currentEmailId)
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun adaptToSmallScreen() {
        binding.run {
            bottomNavigation.isVisible = true
            bottomNavigationFab.isVisible = true
            modalNavigationView.isGone = true
            navigationRail.isGone = true
            standardNavigationView.isGone = true
        }
    }

    private fun adaptToMediumAndLargeScreen() {
        binding.run {
            bottomNavigation.isGone = true
            bottomNavigationFab.isGone = true
            modalNavigationView.isVisible = true
            navigationRail.isVisible = true
            standardNavigationView.isGone = true
        }
    }

    private fun adaptToXLargeScreen() {
        binding.run {
            bottomNavigation.isGone = true
            bottomNavigationFab.isGone = true
            modalNavigationView.isGone = true
            navigationRail.isGone = true
            standardNavigationView.isVisible = true
        }
    }

    private fun setUpNavigationComponentry() {
        binding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                    this@MainActivity
            )

            // Set a custom animation for showing and hiding the FAB
            bottomNavigationFab.apply {
                setShowMotionSpecResource(R.animator.fab_show)
                setHideMotionSpecResource(R.animator.fab_hide)
                setOnClickListener {
                    navigateToCompose()
                }
            }

            // Open the modal navigation drawer on click of the nav rail menu button
            navigationRail.headerView
                ?.findViewById<ImageButton>(R.id.navigation_button)
                ?.setOnClickListener {
                    drawerLayout.openDrawer(modalNavigationView)
                }

            // Close the modal navigation drawer on click of its navigation button
            modalNavigationView.getHeaderView(0)
                    .findViewById<ImageButton>(R.id.navigation_button)
                    .setOnClickListener {
                        drawerLayout.closeDrawer(modalNavigationView)
                    }

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            // Configure all destination selected listeners
            bottomNavigation.setOnItemSelectedListener { item ->
                onNavigationMenuItemChanged(item.itemId)
                true
            }
            navigationRail.setOnItemSelectedListener {
                onNavigationMenuItemChanged(it.itemId)
                true
            }
            modalNavigationView.setNavigationItemSelectedListener {
                onNavigationMenuItemChanged(it.itemId)
                drawerLayout.closeDrawer(modalNavigationView)
                true
            }
            standardNavigationView.setNavigationItemSelectedListener {
                onNavigationMenuItemChanged(it.itemId)
                true
            }
        }
    }

    private fun onNavigationMenuItemChanged(itemId: Int) {
        if (suspendNavigationItemChangedUpdates) return

        suspendNavigationItemChangedUpdates = true
        binding.bottomNavigation.selectedItemId = itemId
        binding.navigationRail.selectedItemId = itemId
        binding.modalNavigationView.setCheckedItem(itemId)
        binding.standardNavigationView.setCheckedItem(itemId)
        suspendNavigationItemChangedUpdates = false
    }
}
