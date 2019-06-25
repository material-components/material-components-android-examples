package com.materialstudies.reply.ui.home

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.materialstudies.reply.App
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentHomeBinding
import com.materialstudies.reply.ui.MenuBottomSheetDialogFragment

/**
 * A [Fragment] that displays a list of emails.
 */
class HomeFragment : Fragment(), EmailAdapter.EmailAdapterListener {

    private lateinit var binding: FragmentHomeBinding

    private val emailAdapter = EmailAdapter(this)
    private lateinit var emailStore: EmailStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fade content out when we navigate to a different screen.
        exitTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.fade_transition)
    }

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
        // Postpone enter transitions to allow shared element transitions to run.
        // https://github.com/googlesamples/android-architecture-components/issues/495
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        emailStore = (requireActivity().application as App).emailStore

        binding.recyclerView.apply {
            val itemTouchHelper = ItemTouchHelper(ReboundingSwipeActionCallback())
            itemTouchHelper.attachToRecyclerView(this)
            adapter = emailAdapter
        }
        binding.recyclerView.adapter = emailAdapter

        emailStore.emails.observe(this, Observer {
            emailAdapter.submitList(it)
        })
    }

    override fun onEmailClicked(cardView: View, email: Email) {
        val extras = FragmentNavigatorExtras(cardView to cardView.transitionName)
        val directions = HomeFragmentDirections.actionHomeFragmentToEmailFragment(email.id)
        findNavController().navigate(directions, extras)
    }

    override fun onEmailLongPressed(email: Email): Boolean {
        MenuBottomSheetDialogFragment(R.menu.email_bottom_sheet_menu) {
            // Do nothing.
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