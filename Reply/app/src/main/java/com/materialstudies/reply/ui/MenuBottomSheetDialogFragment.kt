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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.materialstudies.reply.R

/**
 * A bottom sheet dialog for displaying a simple list of action items.
 */
class MenuBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var navigationView: NavigationView
    @MenuRes private var menuResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuResId = arguments?.getInt(KEY_MENU_RES_ID, 0) ?: 0
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
                R.layout.menu_bottom_sheet_dialog_layout,
                container,
                false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById(R.id.navigation_view)
        navigationView.inflateMenu(menuResId)
        navigationView.setNavigationItemSelectedListener {
            dismiss()
            true
        }
    }

    companion object {

        private const val KEY_MENU_RES_ID = "MenuBottomSheetDialogFragment_menuResId"

        fun newInstance(@MenuRes menuResId: Int): MenuBottomSheetDialogFragment {
            val fragment = MenuBottomSheetDialogFragment()
            val bundle = Bundle().apply {
                putInt(KEY_MENU_RES_ID, menuResId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

