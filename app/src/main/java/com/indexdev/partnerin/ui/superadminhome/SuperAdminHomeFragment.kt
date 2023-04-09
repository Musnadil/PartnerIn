package com.indexdev.partnerin.ui.superadminhome

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.indexdev.partnerin.databinding.FragmentSuperAdminHomeBinding


class SuperAdminHomeFragment : Fragment() {
    private var _binding: FragmentSuperAdminHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuperAdminHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        setupButton()
    }

    private fun setupButton() {
        binding.apply {
            btnApproved.setOnClickListener {
                btnWaiting.strokeColor = ColorStateList.valueOf(Color.rgb(0, 173, 181))
                btnWaiting.strokeWidth = 3
                btnWaiting.setTextColor(Color.parseColor("#00ADB5"))
                btnWaiting.setBackgroundColor(Color.parseColor("#EEEEEE"))

                btnApproved.strokeWidth = 0
                btnApproved.setTextColor(Color.parseColor("#EEEEEE"))
                btnApproved.setBackgroundColor(Color.parseColor("#00ADB5"))

            }
            btnWaiting.setOnClickListener {
                btnApproved.strokeColor = ColorStateList.valueOf(Color.rgb(0, 173, 181))
                btnApproved.strokeWidth = 3
                btnApproved.setTextColor(Color.parseColor("#00ADB5"))
                btnApproved.setBackgroundColor(Color.parseColor("#EEEEEE"))

                btnWaiting.strokeWidth = 0
                btnWaiting.setTextColor(Color.parseColor("#EEEEEE"))
                btnWaiting.setBackgroundColor(Color.parseColor("#00ADB5"))
            }
        }
    }
}