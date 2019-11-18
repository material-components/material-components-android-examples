package com.materialstudies.reply.ui.nav

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.materialstudies.reply.R
import com.materialstudies.reply.databinding.FragmentRailNavigationBinding
import com.materialstudies.reply.util.FastOutUltraSlowIn
import com.materialstudies.reply.util.lerp
import kotlin.math.abs

class RailNavigationFragment : Fragment(), NavigationAdapter.NavigationAdapterListener {

    /**
     * Enumeration of states in which the account picker can be in.
     */
    enum class RailState {

        /**
         * The account picker is not visible. The navigation drawer is in its default state.
         */
        CLOSED,

        /**
         * the account picker is visible and open.
         */
        OPEN,

        /**
         * The account picker sandwiching animation is running. The account picker is neither open
         * nor closed.
         */
        SETTLING
    }

    private lateinit var binding: FragmentRailNavigationBinding

    private var railState: RailState = RailState.OPEN
    private var railAnim: ValueAnimator? = null
    private val railInterp = FastOutUltraSlowIn()
    private var railProgress: Float = 1F
        set(value) {
            if (field != value) {
                onRailProgressChanged(value)
                val newState = when (value) {
                    1F -> RailState.OPEN
                    0F -> RailState.CLOSED
                    else -> RailState.SETTLING
                }
                if (railState != newState) onRailStateChanged(newState)
                railState = newState
                field = value
            }
        }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRailNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            val adapter = NavigationAdapter(this@RailNavigationFragment)
            navRecyclerView.adapter = adapter
            NavigationModel.navigationList.observe(this@RailNavigationFragment) {
                adapter.submitList(it)
            }
            NavigationModel.setNavigationMenuItemChecked(0)

            composeFab.setOnClickListener { toggleRail() }
        }
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        // TODO
    }

    override fun onNavEmailFolderClicked(folder: NavigationModelItem.NavEmailFolder) {
        // TODO
    }

    private fun toggleRail() {
        val initialProgress = railProgress
        val newProgress = when (railState) {
            RailState.CLOSED -> 1F
            RailState.OPEN -> 0F
            RailState.SETTLING -> return
        }
        railAnim?.cancel()
        railAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener { railProgress = animatedValue as Float }
            interpolator = railInterp
            duration = (abs(newProgress - initialProgress) * 250F).toLong()
        }
        railAnim?.start()
    }

    private fun onRailProgressChanged(progress: Float) {
        // TODO
        val railWidth = lerp(
                resources.getDimension(R.dimen.min_rail_nav_width),
                resources.getDimension(R.dimen.max_rail_nav_width),
                0F,
                1F,
                progress
        )

        binding.run {
            val params = railContainer.layoutParams
            params.width = railWidth.toInt()
            railContainer.layoutParams = params

            settingsIcon.alpha = progress
            logoTitleTextView.alpha = progress
        }
    }

    private fun onRailStateChanged(state: RailState) {
        // TODO: Look into animating the height of the fab manually instead of shrinking.
//        when (state) {
//            RailState.CLOSED -> {
//                binding.composeFab.shrink()
//            }
//        }
    }
}