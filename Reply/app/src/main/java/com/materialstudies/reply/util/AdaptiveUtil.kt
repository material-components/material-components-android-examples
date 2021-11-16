/*
 * Copyright 2021 Google LLC
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
package com.materialstudies.reply.util

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.materialstudies.reply.R

object AdaptiveUtil {

    // Based off of: https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes
    private const val SMALL_SCREEN_SIZE = 600
    private const val MEDIUM_SCREEN_SIZE = 840

    /**
     * Updates the navigation and shared activity view based on the Device Orientation
     */
    fun updateNavigationViews(
        screenWidth: Int,
        fab: FloatingActionButton,
        navigationFab: ExtendedFloatingActionButton,
        drawerLayout: DrawerLayout,
        bottomNavigation: BottomNavigationView,
        navigationRail: NavigationRailView,
        navigationDrawer: NavigationView,
        modalDrawer: NavigationView
    ) {
        setUpNavigationDrawer(drawerLayout, navigationRail, modalDrawer)
        when {
            // Small Screen
            screenWidth < SMALL_SCREEN_SIZE -> {
                adaptToSmallScreen(
                    fab,
                    bottomNavigation,
                    navigationDrawer,
                    navigationRail,
                )
            }
            // Medium Screen
            screenWidth in SMALL_SCREEN_SIZE until MEDIUM_SCREEN_SIZE -> {
                adaptToMediumScreen(
                    fab,
                    navigationFab,
                    bottomNavigation,
                    navigationRail,
                    navigationDrawer,
                )
            }
            // Large and Extra Large Screens
            else -> {
                adaptToLargeScreen(
                    fab,
                    navigationFab,
                    bottomNavigation,
                    navigationRail,
                    navigationDrawer
                )
            }
        }
    }

    /**
     * Adapts the Views to a Small Screen
     */
    private fun adaptToSmallScreen(
        fab: FloatingActionButton,
        bottomNavigation: BottomNavigationView,
        navigationDrawer: NavigationView,
        navigationRail: NavigationRailView,
    ) {
        navigationRail.headerView.apply {  }
        fab.visibility = View.GONE
        navigationDrawer.visibility = View.GONE
        navigationRail.visibility = View.GONE
        bottomNavigation.visibility = View.VISIBLE
    }

    /**
     * Adapts the Views to a Medium Screen
     */
    private fun adaptToMediumScreen(
        fab: FloatingActionButton,
        navigationFab: ExtendedFloatingActionButton,
        bottomNavigation: BottomNavigationView,
        navigationRail: NavigationRailView,
        navigationDrawer: NavigationView
    ) {
        fab.visibility = View.GONE
        bottomNavigation.visibility = View.GONE
        navigationDrawer.visibility = View.GONE
        navigationRail.visibility = View.VISIBLE
        navigationFab.shrink()
    }

    /**
     * Adapts the Views to a Large Screen
     */
    private fun adaptToLargeScreen(
        fab: FloatingActionButton,
        navigationFab: ExtendedFloatingActionButton,
        bottomNavigation: BottomNavigationView,
        navigationRail: NavigationRailView,
        navigationDrawer: NavigationView,
    ) {
        fab.visibility = View.GONE
        bottomNavigation.visibility = View.GONE
        navigationRail.visibility = View.GONE
        navigationDrawer.visibility = View.VISIBLE
        navigationFab.extend()
    }

    /**
     * Sets up the Navigation Drawer, Rail and Modal Drawer
     */
    private fun setUpNavigationDrawer(
        drawerLayout: DrawerLayout,
        navigationRail: NavigationRailView,
        modalDrawer: NavigationView
    ) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        setNavRailButtonClickListener(drawerLayout, navigationRail.headerView!!.findViewById(R.id.navigation_button), modalDrawer)
        setModalDrawerButtonOnClickListener(drawerLayout, modalDrawer.getHeaderView(0).findViewById(R.id.navigation_button), modalDrawer)
        modalDrawer.setNavigationItemSelectedListener { item ->
            modalDrawer.setCheckedItem(item)
            drawerLayout.closeDrawer(modalDrawer)
            true
        }
    }

    /**
     * Sets the Navigation Rail navigation button
     */
    private fun setNavRailButtonClickListener(
        drawerLayout: DrawerLayout,
        navButton: View,
        navDrawer: NavigationView
    ) {
        navButton.setOnClickListener {
            drawerLayout.openDrawer(navDrawer)
        }
    }

    /**
     * Sets up the Modal Navigation Drawer navigation button
     */
    private fun setModalDrawerButtonOnClickListener(
        drawerLayout: DrawerLayout,
        button: View,
        modalDrawer: NavigationView
    ) {
        button.setOnClickListener { drawerLayout.closeDrawer(modalDrawer) }
    }

}