package com.materialstudies.reply.ui.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.materialstudies.reply.App
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentEmailBinding
import kotlin.LazyThreadSafetyMode.NONE

private const val MAX_GRID_SPANS = 3

class EmailFragment : Fragment() {

    private lateinit var binding: FragmentEmailBinding

    private val emailId: Int by lazy(NONE) { navArgs<EmailFragmentArgs>().value.emailId }
    private lateinit var emailStore: EmailStore

    private val attachmentAdapter = EmailAttachmentGridAdapter(MAX_GRID_SPANS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailStore = (requireActivity().application as App).emailStore

        binding.navigationIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        val email = emailStore.get(emailId)
        if (email == null) {
            showError()
            return
        }

        binding.run {
            this.email = email

            // Set up the staggered/masonry grid recycler
            attachmentRecyclerView.layoutManager = GridLayoutManager(
                requireContext(),
                MAX_GRID_SPANS
            ).apply {
                spanSizeLookup = attachmentAdapter.variableSpanSizeLookup
            }
            attachmentRecyclerView.adapter = attachmentAdapter
            attachmentAdapter.submitList(email.attachments)
        }
    }


    private fun showError() {
        // TODO: Show error finding email
    }
}