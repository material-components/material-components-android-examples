package com.materialstudies.reply.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.materialstudies.reply.R
import com.materialstudies.reply.ui.nav.AlphaSlideAction
import com.materialstudies.reply.ui.nav.BottomNavigationDrawer
import com.materialstudies.reply.ui.nav.ChangeSettingsMenuStateAction
import com.materialstudies.reply.ui.nav.QuarterRotateSlideAction
import com.materialstudies.reply.ui.nav.ShowHideFabStateAction

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomAppBarContentContainer: LinearLayout
    private lateinit var bottomAppBarChevron: AppCompatImageView
    private lateinit var bottomAppBarTitleTextView: AppCompatTextView
    private lateinit var bottomNavigationDrawer: BottomNavigationDrawer
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Draw behind all system bars
        val decor = window.decorView
        val flags = decor.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        decor.systemUiVisibility = flags

        coordinatorLayout = findViewById(R.id.coordinator_layout)
        bottomAppBar = findViewById(R.id.bottom_app_bar)
        bottomAppBarContentContainer = findViewById(R.id.bottom_app_bar_content_container)
        bottomAppBarChevron = findViewById(R.id.bottom_app_bar_chevron)
        bottomAppBarTitleTextView = findViewById(R.id.bottom_app_bar_title)
        bottomNavigationDrawer = findViewById(R.id.bottom_navigation_drawer)
        fab = findViewById(R.id.fab)

        ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout) { _, insets ->
            handleApplyWindowInsets(insets)
        }

        setUpBottomNavigationAndFab()
    }

    fun handleMenuBottomSheetItemClicked(entry: MenuBottomSheetDialogFragment.MenuEntry) {
        Toast.makeText(this, getString(entry.title), Toast.LENGTH_SHORT).show()
    }

    private fun handleApplyWindowInsets(insets: WindowInsetsCompat): WindowInsetsCompat {
        bottomAppBar.layoutParams.height = getBottomAppBarHeight(this, insets)
        return insets
    }

    private fun setUpBottomNavigationAndFab() {
        fab.setShowMotionSpecResource(R.animator.fab_show)
        fab.setHideMotionSpecResource(R.animator.fab_hide)

        bottomNavigationDrawer.apply {
            addOnSlideAction(QuarterRotateSlideAction(bottomAppBarChevron))
            addOnSlideAction(AlphaSlideAction(bottomAppBarTitleTextView, true))
            addOnStateChangedAction(ShowHideFabStateAction(fab))
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                bottomAppBar.replaceMenu(if (showSettings) {
                    R.menu.bottom_app_bar_settings_menu
                } else {
                    R.menu.bottom_app_bar_home_menu
                })
            })
        }

        bottomAppBar.setNavigationOnClickListener { bottomNavigationDrawer.toggle() }
        bottomAppBar.setOnMenuItemClickListener(this)
        bottomAppBarContentContainer.setOnClickListener { bottomNavigationDrawer.toggle() }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_theme -> toggleTheme()
        }
        return true
    }

    private fun toggleTheme() {
        delegate.localNightMode = if (delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    companion object {
        fun getBottomAppBarHeight(context: Context, insets: WindowInsetsCompat): Int {
            return context.resources.getDimensionPixelSize(
                R.dimen.bottom_app_bar_height
            ) + insets.systemWindowInsetBottom
        }
    }
}
