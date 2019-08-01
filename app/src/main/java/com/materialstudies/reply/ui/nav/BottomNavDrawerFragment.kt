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

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.google.android.material.shape.MaterialShapeDrawable
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentBottomNavDrawerBinding
import com.materialstudies.reply.util.getColorFromAttr
import kotlin.LazyThreadSafetyMode.NONE

/**
 * A [Fragment] which acts as a bottom navigation drawer.
 */
class BottomNavDrawerFragment : Fragment(), NavigationAdapter.NavigationAdapterListener {

    private lateinit var binding: FragmentBottomNavDrawerBinding

    private val behavior: BottomSheetBehavior<FrameLayout> by lazy(NONE) {
        from(binding.backgroundContainer)
    }

    private val bottomSheetCallback = BottomNavigationDrawerCallback()

    private val backgroundShapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        MaterialShapeDrawable(
            binding.backgroundContainer.context,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().getColorFromAttr(R.attr.colorBrandedVariantSurface)
            )
            elevation = resources.getDimension(R.dimen.plane_08)
            initializeElevationOverlay(requireContext())
        }
    }

    private val foregroundShapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        MaterialShapeDrawable(
            binding.foregroundContainer.context,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().getColorFromAttr(R.attr.colorBrandedSurface)
            )
            elevation = resources.getDimension(R.dimen.plane_16)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
            initializeElevationOverlay(requireContext())
            shapeAppearanceModel.topEdge = SemiCircleEdgeCutoutTreatment(
                resources.getDimension(R.dimen.keyline_3),
                resources.getDimension(R.dimen.keyline_5),
                0F,
                resources.getDimension(R.dimen.navigation_drawer_profile_image_size)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomNavDrawerBinding.inflate(inflater, container, false)
        binding.foregroundContainer.setOnApplyWindowInsetsListener { view, windowInsets ->
            // Record the window's top inset so it can be applied when the bottom sheet is slide up
            // to meet the top edge of the screen.
            view.setTag(
                R.id.tag_system_window_inset_top,
                windowInsets.systemWindowInsetTop
            )
            windowInsets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            backgroundContainer.background = backgroundShapeDrawable
            foregroundContainer.background = foregroundShapeDrawable

            scrimView.setOnClickListener { close() }

            bottomSheetCallback.apply {
                // Scrim view transforms
                addOnSlideAction(AlphaSlideAction(scrimView))
                addOnStateChangedAction(VisibilityStateAction(scrimView))
                // Foreground transforms
                addOnSlideAction(ForegroundSheetTransformSlideAction(
                    binding.foregroundContainer,
                    foregroundShapeDrawable,
                    binding.profileImageView
                ))
                // Recycler transforms
                addOnStateChangedAction(ScrollToTopStateAction(navRecyclerView))
            }

            behavior.setBottomSheetCallback(bottomSheetCallback)
            behavior.state = STATE_HIDDEN

            val adapter = NavigationAdapter(this@BottomNavDrawerFragment)
            binding.navRecyclerView.adapter = adapter
            NavigationModel.navigationList.observe(this@BottomNavDrawerFragment) {
                adapter.submitList(it)
            }
            NavigationModel.setNavigationMenuItemChecked(0)
        }
    }

    fun toggle() {
        when (behavior.state) {
            STATE_HIDDEN -> open()
            STATE_HALF_EXPANDED, STATE_EXPANDED, STATE_COLLAPSED -> close()
        }
    }

    fun open() {
        behavior.state = STATE_HALF_EXPANDED
    }

    fun close() {
        behavior.state = STATE_HIDDEN
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        if (NavigationModel.setNavigationMenuItemChecked(item.id)) close()
    }

    override fun onNavEmailFolderClicked(folder: NavigationModelItem.NavEmailFolder) {
        // Do nothing
    }
}