package com.materialstudies.reply.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.MainActivity
import com.materialstudies.reply.ui.MenuBottomSheetDialogFragment

class HomeFragment : Fragment(), EmailAdapter.EmailAdapterListener {


    private lateinit var recyclerView: RecyclerView

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
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            handleApplyWindowInsets(insets)
        }

        val adapter = EmailAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.submitList(EmailStore.emails)
    }

    private fun handleApplyWindowInsets(insets: WindowInsetsCompat): WindowInsetsCompat {
        recyclerView.updatePadding(
            insets.systemWindowInsetLeft,
            insets.systemWindowInsetTop + resources.getDimensionPixelSize(R.dimen.keyline_2),
            insets.systemWindowInsetRight,
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
}