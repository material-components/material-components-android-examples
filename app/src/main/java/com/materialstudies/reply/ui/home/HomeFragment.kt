package com.materialstudies.reply.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.reply.App
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.MainActivity
import com.materialstudies.reply.ui.MenuBottomSheetDialogFragment

class HomeFragment : Fragment(), EmailAdapter.EmailAdapterListener {

    private lateinit var recyclerView: RecyclerView

    private val adapter = EmailAdapter(this)
    private lateinit var emailStore: EmailStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)

        emailStore = (requireActivity().application as App).emailStore

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            handleApplyWindowInsets(insets)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        emailStore.emails.observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private fun handleApplyWindowInsets(insets: WindowInsetsCompat): WindowInsetsCompat {
        val keyline2 = resources.getDimensionPixelSize(R.dimen.keyline_2)
        recyclerView.updatePadding(
            insets.systemWindowInsetLeft + keyline2,
            insets.systemWindowInsetTop + keyline2,
            insets.systemWindowInsetRight + keyline2,
            MainActivity.getBottomAppBarHeight(requireContext(), insets)
        )
        return insets
    }

    override fun onEmailClicked(email: Email) {
        // TODO: Navigate to DetailsFragment
    }

    override fun onEmailLongPressed(email: Email): Boolean {
        MenuBottomSheetDialogFragment().show(requireFragmentManager(), null)
        return true
    }

    override fun onEmailStarChanged(email: Email, newValue: Boolean) {
        emailStore.update(email.id) { isStarred = newValue }
    }

    override fun onEmailArchived(email: Email) {
        emailStore.delete(email.id)
    }

}