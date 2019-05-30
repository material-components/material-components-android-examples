package com.materialstudies.reply.util

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

/**
 * An extension function to iterate through a Menu's MenuItems.
 */
inline fun Menu.forEachMenuItem(action: (MenuItem) -> Unit) {
    MenuIterator(this).forEach(action)
}

class MenuIterator(private val menu: Menu): Iterator<MenuItem> {

    private var current = 0

    override fun hasNext(): Boolean =
            menu.size() > current

    override fun next(): MenuItem {
        return menu.getItem(current++)
    }

}
