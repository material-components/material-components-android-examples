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

/**
 * An extension value to get an iterator to allow iterating through all of a ViewGroup's children.
 */
val ViewGroup.children: Iterator<View>
    get() = ViewGroupChildIterator(this)

class ViewGroupChildIterator(private val viewGroup: ViewGroup): Iterator<View> {

    private var current: Int = 0

    override fun hasNext(): Boolean = viewGroup.childCount > current

    override fun next(): View {
        val i = current
        current++
        return viewGroup.getChildAt(i)
    }

}