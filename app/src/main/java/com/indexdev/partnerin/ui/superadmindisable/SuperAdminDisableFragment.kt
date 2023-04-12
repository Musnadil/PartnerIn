package com.indexdev.partnerin.ui.superadmindisable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indexdev.partnerin.R
import com.indexdev.partnerin.databinding.FragmentSuperAdminDisableBinding

class SuperAdminDisableFragment : Fragment() {
    private var _binding: FragmentSuperAdminDisableBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuperAdminDisableBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        val navigationBarHeight =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (navigationBarHeight > 0) {
            binding.bottomNavBar.layoutParams.height =
                resources.getDimensionPixelSize(navigationBarHeight)
        }
    }
}