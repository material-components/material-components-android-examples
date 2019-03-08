package com.materialstudies.reply.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore

class MainActivity : AppCompatActivity(), EmailAdapter.EmailAdapterListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomAppBarContentContainer: LinearLayout
    private lateinit var bottomAppBarChevron: AppCompatImageView
    private lateinit var bottomNavigationDrawer: BottomNavigationDrawer
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        bottomAppBar = findViewById(R.id.bottom_app_bar)
        bottomAppBarContentContainer = findViewById(R.id.bottom_app_bar_content_container)
        bottomAppBarChevron = findViewById(R.id.bottom_app_bar_chevron)
        bottomNavigationDrawer = findViewById(R.id.bottom_navigation_drawer)
        fab = findViewById(R.id.fab)

        setSupportActionBar(bottomAppBar)

        setUpBottomNavigationAndFab()

        setUpEmailRecycler()

    }

    /**
     * Configure the RecyclerView which holds the list of [Email]s.
     */
    private fun setUpEmailRecycler() {
        val adapter = EmailAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.submitList(EmailStore.emails)
    }

    override fun onEmailClicked(email: Email) {
        // Do nothing
    }

    override fun onEmailLongPressed(email: Email): Boolean {
        MenuBottomSheetDialogFragment().show(supportFragmentManager, null)
        return true
    }

    fun handleMenuBottomSheetItemClicked(entry: MenuBottomSheetDialogFragment.MenuEntry) {
        Toast.makeText(this, getString(entry.title), Toast.LENGTH_SHORT).show()
    }

    private fun setUpBottomNavigationAndFab() {
        fab.setShowMotionSpecResource(R.animator.fab_show)
        fab.setHideMotionSpecResource(R.animator.fab_hide)

        bottomNavigationDrawer.setNavigationDrawerCallback(
                NavigationBottomDrawerCallback(fab, bottomAppBarChevron)
        )

        bottomAppBar.setNavigationOnClickListener { bottomNavigationDrawer.toggle() }
        bottomAppBarContentContainer.setOnClickListener { bottomNavigationDrawer.toggle() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_app_bar_home_menu, menu)
        return true
    }


}
