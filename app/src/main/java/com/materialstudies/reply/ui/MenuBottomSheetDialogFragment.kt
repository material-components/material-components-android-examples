package com.materialstudies.reply.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.materialstudies.reply.R

class MenuBottomSheetDialogFragment(
    private val menuRes: Int,
    private val onNavigationItemSelected: (MenuItem) -> Boolean
) : BottomSheetDialogFragment() {

    private lateinit var navigationView: NavigationView

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
        navigationView.inflateMenu(menuRes)
        navigationView.setNavigationItemSelectedListener {
            val consumed = onNavigationItemSelected(it)
            if (consumed) dismiss()
            consumed
        }
    }
}

