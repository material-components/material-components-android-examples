package com.materialstudies.reply.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.materialstudies.reply.R
import com.materialstudies.reply.util.ColorUtils
import com.materialstudies.reply.util.children
import com.materialstudies.reply.util.forEachMenuItem

/**
 * A composite FrameLayout to hold all logic dealing with the BottomSheet which acts as the
 * navigation drawer.
 */
class BottomNavigationDrawer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val profileImageView: AppCompatImageView
    private val navigationMenuContainer: LinearLayout

    private val navigationMenu = PopupMenu(context, null).menu

    private val behavior = BottomSheetBehavior<FrameLayout>(context, attrs).apply {
        isHideable = true
        skipCollapsed = true
    }

    init {
        val view = View.inflate(context, R.layout.bottom_navigation_drawer_layout, this)
        profileImageView = view.findViewById(R.id.profile_image_view)
        navigationMenuContainer = view.findViewById(R.id.navigation_menu_container)

        clipToPadding = true

        // Set the background of the drawer's background sheet.
        ViewCompat.setBackground(
                this,
                ContextCompat.getDrawable(context, R.drawable.navigation_drawer_background)
        )
        // Set the background of the drawer's foregroun sheet
        ViewCompat.setBackground(
                navigationMenuContainer,
                createNavigationMenuForegroundShapeDrawable(context)
        )

        inflateNavigationMenu()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val params: CoordinatorLayout.LayoutParams = layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = behavior
        layoutParams = params

        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun inflateNavigationMenu() {
        MenuInflater(context).inflate(R.menu.navigation_drawer_menu, navigationMenu)
        navigationMenu.forEachMenuItem { menuItem ->
            val navigationItemView = LayoutInflater.from(context).inflate(
                    R.layout.navigation_menu_item_layout,
                    navigationMenuContainer,
                    false
            )
            navigationItemView.tag = menuItem.itemId

            navigationItemView.findViewById<AppCompatImageView>(
                    R.id.menu_item_icon
            ).setImageDrawable(menuItem.icon)

            navigationItemView.findViewById<AppCompatTextView>(
                    R.id.menu_item_title
            ).text = menuItem.title

            navigationItemView.setOnClickListener {
                setNavigationMenuItemSelected(menuItem.itemId)
            }
            navigationMenuContainer.addView(navigationItemView)
        }

        setNavigationMenuItemSelected(navigationMenu.getItem(0).itemId)
    }

    private fun setNavigationMenuItemSelected(id: Int) {
        navigationMenuContainer.children.forEach { view ->
            val color = ColorStateList.valueOf(if (view.tag == id) {
                ColorUtils.getColorFromAttr(context, R.attr.colorSecondary)
            } else {
                Color.WHITE
            })

            view.findViewById<AppCompatImageView>(R.id.menu_item_icon).imageTintList = color
            view.findViewById<AppCompatTextView>(R.id.menu_item_title).setTextColor(color)
        }
    }

    /**
     * Open the drawer if it's closed., close the drawer if it's open. If the drawer is between
     * closed and opened states, this method does nothing.
     */
    fun toggle() {
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED
                || behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    /**
     * Register to listen to this drawer's BottomSheetCallback.
     */
    fun setNavigationDrawerCallback(
            navigationDrawerCallback: BottomSheetBehavior.BottomSheetCallback
    ) {
        behavior.setBottomSheetCallback(navigationDrawerCallback)
    }

    /**
     * Create a MaterialShapeDrawable which has rounded corners and a cutout to cradle the
     * navigation drawer's profile image view.
     */
    private fun createNavigationMenuForegroundShapeDrawable(
            context: Context
    ): MaterialShapeDrawable {
        val topEdgeTreatment = SemiCircleEdgeCutoutTreatment(
                resources.getDimension(R.dimen.keyline3),
                resources.getDimension(R.dimen.keyline5),
                0F,
                resources.getDimension(R.dimen.navigation_drawer_profile_image_size)
        )

        val cornerTreatment = RoundedCornerTreatment(
                resources.getDimension(R.dimen.large_component_corner_radius)
        )

        val shape = MaterialShapeDrawable()
        shape.shapeAppearanceModel.apply {
            topEdge = topEdgeTreatment
            topLeftCorner = cornerTreatment
            topRightCorner = cornerTreatment
        }
        shape.paintStyle = Paint.Style.FILL
        shape.elevation = 0F
        DrawableCompat.setTintList(
                shape,
                ColorStateList.valueOf(ColorUtils.getColorFromAttr(context, R.attr.colorPrimary))
        )
        return shape
    }
}