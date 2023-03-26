package com.indexdev.partnerin.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        binding.btnEditAccount.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editAccountFragment)
        }
        binding.btnAccountSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        }
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Anda yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya") { positive, _ ->
                    positive.dismiss()
                }
                .setNegativeButton("Tidak") { negative, _ ->
                    negative.dismiss()
                }
                .show()
        }
    }
}