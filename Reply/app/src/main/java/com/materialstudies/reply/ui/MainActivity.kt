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
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.ActivityMainBinding
import com.materialstudies.reply.ui.compose.ComposeFragmentDirections
import com.materialstudies.reply.ui.email.EmailFragmentArgs
import com.materialstudies.reply.ui.home.HomeFragmentDirections
import com.materialstudies.reply.ui.home.Mailbox
import com.materialstudies.reply.ui.nav.NavigationAdapter
import com.materialstudies.reply.ui.nav.NavigationModelItem
import com.materialstudies.reply.ui.search.SearchFragmentDirections
import com.materialstudies.reply.util.AdaptiveUtils
import com.materialstudies.reply.util.contentView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.SMALL
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.MEDIUM
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.LARGE
import com.materialstudies.reply.util.AdaptiveUtils.ScreenSize.XLARGE

class MainActivity : AppCompatActivity(),
                     Toolbar.OnMenuItemClickListener,
                     NavController.OnDestinationChangedListener,
                     NavigationAdapter.NavigationAdapterListener {

    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    // Keep track of the current Email being viewed, if any, in order to pass the correct email id
    // to ComposeFragment when this Activity's FAB is clicked.
    private var currentEmailId = -1L

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.childFragmentManager
                ?.fragments
                ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyIfAvailable(this)
        applyBackgroundColor()
        super.onCreate(savedInstanceState)
        setUpBottomNavigationAndFab()

        val surfaceColor5 = SurfaceColors.SURFACE_5.getColor(this)
        binding.modalNavDrawer.setBackgroundColor(surfaceColor5)
        setUpNavigationDrawer(binding.drawerLayout, binding.navRail, binding.modalNavDrawer)

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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                showDarkThemeMenu()
            }
            R.id.menu_search -> navigateToSearch()
            R.id.menu_email_star -> {
                EmailStore.update(currentEmailId) { isStarred = !isStarred }
            }
            R.id.menu_email_delete -> {
                EmailStore.delete(currentEmailId)
                findNavController(R.id.nav_host_fragment).popBackStack()
            }
        }
        return true
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

    private fun showDarkThemeMenu() {
        MenuBottomSheetDialogFragment
            .newInstance(R.menu.dark_theme_bottom_sheet_menu)
            .show(supportFragmentManager, null)
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

    private fun applyBackgroundColor() {
        val surfaceColor5 = SurfaceColors.SURFACE_5.getColor(this)
        window.decorView.setBackgroundColor(surfaceColor5)
    }

    private fun setUpBottomNavigationAndFab() {
        // Wrap binding.run to ensure ContentViewBindingDelegate is calling this Activity's
        // setContentView before accessing views
        binding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
        }

        // Set a custom animation for showing and hiding the FAB
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                navigateToCompose()
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inbox -> {
                    // TODO: Update navigate to home.
                    true
                }
                R.id.menu_articles -> {
                    // TODO: Update navigate to placeholder fragment
                    true
                }
                R.id.menu_chat -> {
                    // TODO: Update navigate to placeholder fragment
                    true
                }
                R.id.menu_video -> {
                    // TODO: Update navigate to placeholder fragment
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun adaptToSmallScreen() {
        binding.run {
            navDrawer.isGone = true
            navRail.isGone = true
            modalNavDrawer.isGone = true
            fab.isVisible = true
            bottomNavigation.isVisible = true
        }
    }

    private fun adaptToMediumAndLargeScreen() {
        binding.run {
            navDrawer.isGone = true
            fab.isGone = true
            bottomNavigation.isGone = true
            navRail.isVisible = true
            modalNavDrawer.isVisible = true
        }
    }

    private fun adaptToXLargeScreen() {
        binding.run {
            fab.isGone = true
            bottomNavigation.isGone = true
            navRail.isGone = true
            modalNavDrawer.isGone = true
            navDrawer.isVisible = true
        }
    }

    private fun setUpNavigationDrawer(
        drawerLayout: DrawerLayout,
        navigationRail: NavigationRailView,
        modalDrawer: NavigationView
    ) {
        // Set Click Listener for the Navigation Rail
        val navRailMenuButton = navigationRail.headerView!!.findViewById<ImageButton>(R.id.navigation_button)
        navRailMenuButton.setOnClickListener { drawerLayout.openDrawer(modalDrawer) }

        // Set Click Listener for the Modal Navigation Drawer
        val modalDrawerNavigationButton = modalDrawer.getHeaderView(0).findViewById<ImageButton>(R.id.navigation_button)
        modalDrawerNavigationButton.setOnClickListener { drawerLayout.closeDrawer(modalDrawer) }

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        modalDrawer.setNavigationItemSelectedListener { item ->
            modalDrawer.setCheckedItem(item)
            drawerLayout.closeDrawer(modalDrawer)
            true
        }
    }
}
