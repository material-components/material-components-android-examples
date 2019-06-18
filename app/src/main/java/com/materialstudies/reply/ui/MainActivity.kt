package com.materialstudies.reply.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.materialstudies.reply.R
import com.materialstudies.reply.databinding.ActivityMainBinding
import com.materialstudies.reply.ui.nav.AlphaSlideAction
import com.materialstudies.reply.ui.nav.ChangeSettingsMenuStateAction
import com.materialstudies.reply.ui.nav.QuarterRotateSlideAction
import com.materialstudies.reply.ui.nav.ShowHideFabStateAction
import com.materialstudies.reply.util.contentView

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBottomNavigationAndFab()
    }

    private fun setUpBottomNavigationAndFab() {
        binding.fab.setShowMotionSpecResource(R.animator.fab_show)
        binding.fab.setHideMotionSpecResource(R.animator.fab_hide)

        binding.bottomNavigationDrawer.apply {
            addOnSlideAction(QuarterRotateSlideAction(binding.bottomAppBarChevron))
            addOnSlideAction(AlphaSlideAction(binding.bottomAppBarTitle, true))
            addOnStateChangedAction(ShowHideFabStateAction(binding.fab))
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                binding.bottomAppBar.replaceMenu(if (showSettings) {
                    R.menu.bottom_app_bar_settings_menu
                } else {
                    R.menu.bottom_app_bar_home_menu
                })
            })
        }

        binding.bottomAppBar.setNavigationOnClickListener {
            binding.bottomNavigationDrawer.toggle()
        }
        binding.bottomAppBar.setOnMenuItemClickListener(this)
        binding.bottomAppBarContentContainer.setOnClickListener {
            binding.bottomNavigationDrawer.toggle()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_theme -> {
                binding.bottomNavigationDrawer.close()
                showDarkThemeMenu()
            }
        }
        return true
    }

    private fun showDarkThemeMenu() {
        MenuBottomSheetDialogFragment(R.menu.dark_theme_bottom_sheet_menu) {
            onDarkThemeMenuItemSelected(it.itemId)
        }.show(supportFragmentManager, null)
    }

    private fun onDarkThemeMenuItemSelected(itemId: Int): Boolean {
        val nightMode = when (itemId) {
            R.id.menu_light -> AppCompatDelegate.MODE_NIGHT_NO
            R.id.menu_dark -> AppCompatDelegate.MODE_NIGHT_YES
            R.id.menu_battery_saver -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            R.id.menu_system_default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> return false
        }

        delegate.localNightMode = nightMode
        return true
    }
}
