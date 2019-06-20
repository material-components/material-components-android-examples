package com.materialstudies.reply.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.materialstudies.reply.App
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentHomeBinding
import com.materialstudies.reply.ui.MenuBottomSheetDialogFragment
import com.materialstudies.reply.ui.email.EmailFragmentArgs

class HomeFragment : Fragment(), EmailAdapter.EmailAdapterListener {

    private lateinit var binding: FragmentHomeBinding

    private val emailAdapter = EmailAdapter(this)
    private lateinit var emailStore: EmailStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailStore = (requireActivity().application as App).emailStore

        binding.recyclerView.adapter = emailAdapter

        emailStore.emails.observe(this, Observer {
            emailAdapter.submitList(it)
        })
    }

    override fun onEmailClicked(email: Email) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToEmailFragment(email.id)
        )
    }

    override fun onEmailLongPressed(email: Email): Boolean {
        MenuBottomSheetDialogFragment(R.menu.email_bottom_sheet_menu) {
            // TODO: Handle on menu item clicks
            true
        }.show(requireFragmentManager(), null)

        return true
    }

    override fun onEmailStarChanged(email: Email, newValue: Boolean) {
        emailStore.update(email.id) { isStarred = newValue }
    }

    override fun onEmailArchived(email: Email) {
        emailStore.delete(email.id)
    }

}