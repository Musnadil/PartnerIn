package com.indexdev.partnerin.ui.forgotpassword

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.indexdev.partnerin.databinding.FragmentVerifyOtpBinding


class VerifyOtpFragment : Fragment() {
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyOtpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etOtpCode.requestFocus()
        binding.btnVerify.setOnClickListener {
            Toast.makeText(requireContext(), binding.etOtpCode.text.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }
}