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

package com.materialstudies.reply.ui.nav

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.use
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.materialstudies.reply.R
import com.materialstudies.reply.util.getColorFromAttr
import java.lang.IllegalStateException

/**
 * A composite FrameLayout to hold all logic dealing with the BottomSheet which acts as the
 * navigation drawer.
 */
class BottomNavigationDrawer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.bottomNavigationDrawerStyle,
        defStyleRes: Int = R.style.Widget_Reply_BottomNavigationDrawer
): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val profileImageView: AppCompatImageView
    private val foregroundContainer: LinearLayout
    private val navigationView: NavigationView

    private val bottomSheetCallback = BottomNavigationDrawerCallback()

    private val behavior = BottomSheetBehavior<FrameLayout>(context, attrs).apply {
        isFitToContents = true
        isHideable = true
        skipCollapsed = true
        setBottomSheetCallback(bottomSheetCallback)
    }

    private val backgroundShape = MaterialShapeDrawable(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ).apply {
        elevation = resources.getDimension(R.dimen.plane_08)
        initializeElevationOverlay(context)
    }

    private lateinit var foregroundShape: MaterialShapeDrawable

    // A view to be added directly above the BottomNavigationDrawer which acts as a scrim when
    // the drawer is open.
    private val scrimView = View(context).apply {
        // Generate an id for the scrim view. If we don't generate an id, the id will be the same
        // as this BottomNavigationSheetView's, making either view impossible to espresso test.
        id = View.generateViewId()
        layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        )
        elevation = this@BottomNavigationDrawer.elevation
        visibility = View.GONE
        alpha = 0F
        setOnClickListener { close() }
    }

    init {
        val view = View.inflate(context, R.layout.bottom_navigation_drawer_layout, this)
        profileImageView = view.findViewById(R.id.profile_image_view)
        foregroundContainer = view.findViewById(R.id.foreground_container)
        navigationView = view.findViewById(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { true }
        navigationView.setCheckedItem(R.id.navigation_menu_inbox)

        // Clear the insets applied by ScrimInsetsFrameLayout, the parent of NavigationView.
        ViewCompat.setOnApplyWindowInsetsListener(navigationView) { _, insets ->
            navigationView.updatePadding(0,0,0,0)
            insets
        }

        clipToPadding = true

        getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.BottomNavigationDrawer,
            defStyleAttr,
            defStyleRes
        ).use {

            val scrimColor = it.getColor(
                R.styleable.BottomNavigationDrawer_scrimColor,
                context.getColorFromAttr(R.attr.scrimBackground)
            )
            scrimView.setBackgroundColor(scrimColor)

            val foregroundShapeAppearance = it.getResourceId(
                R.styleable.BottomNavigationDrawer_foregroundShapeAppearance,
                R.style.ShapeAppearance_MaterialComponents_LargeComponent
            )
            val foregroundShapeAppearanceOverlay = it.getResourceId(
                R.styleable.BottomNavigationDrawer_foregroundShapeAppearanceOverlay,
                0
            )
            val foregroundFillColor = it.getColor(
                R.styleable.BottomNavigationDrawer_android_foregroundTint,
                0
            )
            foregroundShape = MaterialShapeDrawable(ShapeAppearanceModel(
                context,
                foregroundShapeAppearance,
                foregroundShapeAppearanceOverlay
            )).apply {
                val topEdgeTreatment = SemiCircleEdgeCutoutTreatment(
                    resources.getDimension(R.dimen.keyline_3),
                    resources.getDimension(R.dimen.keyline_5),
                    0F,
                    resources.getDimension(R.dimen.navigation_drawer_profile_image_size)
                )
                shapeAppearanceModel.topEdge = topEdgeTreatment
                fillColor = ColorStateList.valueOf(foregroundFillColor)
                elevation = resources.getDimension(R.dimen.plane_16)
                initializeElevationOverlay(context)
            }
        }

        // Set the background of the drawer's background sheet.
        ViewCompat.setBackground(
            this,
            backgroundShape
        )

        // Set the background of the drawer's foreground sheet
        ViewCompat.setBackground(
            foregroundContainer,
            foregroundShape
        )
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val params: CoordinatorLayout.LayoutParams = layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = behavior
        layoutParams = params

        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        addScrimView()
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        foregroundContainer.updatePadding(
            bottom = insets.systemWindowInsetBottom +
                resources.getDimensionPixelSize(R.dimen.bottom_app_bar_height)
        )
        return super.onApplyWindowInsets(insets)
    }

    private fun addScrimView() {
        if (scrimView.isAttachedToWindow) return

        val parentCoordinator = parent as? CoordinatorLayout ?: throw IllegalStateException(
            "BottomNavigationDrawer must be a direct child of a CoordinatorLayout"
        )

        bottomSheetCallback.addOnSlideAction(AlphaSlideAction(scrimView))
        bottomSheetCallback.addOnStateChangedAction(VisibilityStateAction(scrimView))

        // Add scrimView directly above BottomNavigationSheetView in the layout hierarchy.
        parentCoordinator.addView(scrimView, parentCoordinator.indexOfChild(this))
    }

    /**
     * Open the drawer if it's closed., close the drawer if it's open. If the drawer is between
     * closed and opened states, this method does nothing.
     */
    fun toggle() {
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            open()
        } else if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED ||
            behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            close()
        }
    }

    fun open() {
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun close() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }
}