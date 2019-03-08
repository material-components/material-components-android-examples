package com.materialstudies.reply.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.materialstudies.reply.R

class MenuBottomSheetDialogFragment : BottomSheetDialogFragment() {

    data class MenuEntry(val title: Int, val icon: Int)

    companion object {
        val menuItems = listOf(
                MenuEntry(R.string.menu_item_forward, R.drawable.ic_forward_24dp),
                MenuEntry(R.string.menu_item_reply, R.drawable.ic_reply_24dp),
                MenuEntry(R.string.menu_item_reply_all, R.drawable.ic_reply_all_24dp),
                MenuEntry(R.string.menu_item_archive, R.drawable.ic_archive_24dp),
                MenuEntry(R.string.menu_item_delete, R.drawable.ic_delete_24dp)
        )
    }

    private lateinit var menuContainer: LinearLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
                R.layout.menu_bottom_sheet_dialog_layout,
                container,
                false
        )
        menuContainer = view.findViewById(R.id.menu_container)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addActions(menuContainer, menuItems)
    }

    private fun addActions(list: ViewGroup, entries: List<MenuEntry>) {
        entries.forEach {entry ->
            val item = layoutInflater.inflate(R.layout.menu_sheet_item_layout, list, false)

            // Set the item's title
            item.findViewById<AppCompatTextView>(
                    R.id.menu_item_title
            ).text = resources.getString(entry.title)

            // Set the item's icon
            item.findViewById<AppCompatImageView>(R.id.menu_item_icon).setImageResource(entry.icon)

            // Set the item's click listener
            item.setOnClickListener {
                (activity as? MainActivity)?.handleMenuBottomSheetItemClicked(entry)
                dismiss()
            }

            list.addView(item)
        }
    }

}

