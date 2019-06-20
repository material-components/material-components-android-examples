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

package com.materialstudies.owl.ui.learn

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.view.doOnLayout
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.FragmentLearnBinding
import com.materialstudies.owl.model.courses
import com.materialstudies.owl.model.lessons
import com.materialstudies.owl.util.doOnApplyWindowInsets
import com.materialstudies.owl.util.lerp
import com.materialstudies.owl.util.lerpArgb

/**
 * A [Fragment] displaying the learn screen.
 */
class LearnFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLearnBinding.inflate(inflater, container, false).apply {
            course = courses.last()

            val behavior = BottomSheetBehavior.from(lessonsSheet)
            val sheetStartColor = lessonsSheet.context.getColor(R.color.owl_pink)
            val sheetEndColor =
                lessonsSheet.context.getColorStateList(R.color.primary_sheet).defaultColor
            val sheetBackground = MaterialShapeDrawable(
                ShapeAppearanceModel(
                    lessonsSheet.context, R.style.ShapeAppearance_Owl_MinimizedSheet, 0
                )
            ).apply {
                fillColor = ColorStateList.valueOf(sheetStartColor)
            }
            lessonsSheet.background = sheetBackground
            lessonsSheet.doOnLayout {
                val peek = behavior.peekHeight
                val maxTranslationX = (it.width - peek).toFloat()
                lessonsSheet.translationX = (lessonsSheet.width - peek).toFloat()

                // Alter views based on the sheet expansion
                behavior.setBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {}
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        lessonsSheet.translationX =
                            lerp(maxTranslationX, 0f, 0f, 0.15f, slideOffset)
                        sheetBackground.interpolation = lerp(1f, 0f, 0f, 0.15f, slideOffset)
                        sheetBackground.fillColor = ColorStateList.valueOf(
                            lerpArgb(
                                sheetStartColor,
                                sheetEndColor,
                                0f,
                                0.3f,
                                slideOffset
                            )
                        )
                        playlistIcon.alpha = lerp(1f, 0f, 0f, 0.15f, slideOffset)
                        sheetExpand.alpha = lerp(1f, 0f, 0f, 0.15f, slideOffset)
                        sheetExpand.visibility = if (slideOffset < 0.5) VISIBLE else GONE
                        playlistTitle.alpha = lerp(0f, 1f, 0.2f, 0.8f, slideOffset)
                        collapsePlaylist.alpha = lerp(0f, 1f, 0.2f, 0.8f, slideOffset)
                        playlistTitleDivider.alpha = lerp(0f, 1f, 0.2f, 0.8f, slideOffset)
                        playlist.alpha = lerp(0f, 1f, 0.2f, 0.8f, slideOffset)
                    }
                })
                lessonsSheet.doOnApplyWindowInsets { _, insets, _, _ ->
                    behavior.peekHeight = peek + insets.systemWindowInsetBottom
                }
            }
            collapsePlaylist.setOnClickListener {
                behavior.state = STATE_COLLAPSED
            }
            sheetExpand.setOnClickListener {
                behavior.state = STATE_EXPANDED
            }
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            playlist.adapter = LessonAdapter().apply {
                submitList(lessons)
            }
            playlist.addItemDecoration(
                InsetDivider(
                    resources.getDimensionPixelSize(R.dimen.divider_inset),
                    resources.getDimensionPixelSize((R.dimen.divider_height)),
                    playlist.context.getColor(R.color.divider)
                )
            )
            alsoLikeList.adapter = RelatedAdapter().apply {
                submitList(courses)
            }
        }
        return binding.root
    }
}

/**
 * An [ItemDecoration] which is inset from the left by the given amount.
 */
class InsetDivider(
    @Px private val inset: Int,
    @Px private val height: Int,
    @ColorInt private val dividerColor: Int
) : RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dividerColor
        style = Paint.Style.STROKE
        strokeWidth = height.toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val points = mutableListOf<Float>()
        parent.forEach {
            if (parent.getChildAdapterPosition(it) < state.itemCount - 1) {
                val bottom = it.bottom.toFloat()
                points.add((it.left + inset).toFloat())
                points.add(bottom)
                points.add(it.right.toFloat())
                points.add(bottom)
            }
        }
        c.drawLines(points.toFloatArray(), dividerPaint)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = height / 2
        outRect.bottom = height / 2
    }
}
