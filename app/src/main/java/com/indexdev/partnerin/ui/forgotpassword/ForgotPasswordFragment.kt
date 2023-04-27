package com.indexdev.partnerin.ui.forgotpassword

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import com.indexdev.partnerin.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private val bundle = Bundle()

    companion object {
        const val EMAIL_BUNDLE = "EMAIL_BUNDLE"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnLogin.setOnClickListener {
            findNavController().popBackStack()
        }
        emailObserver()
        binding.btnSend.setOnClickListener {
            requestForgotPassword()
        }
    }

    private fun emailObserver() {
        if (viewModel.responseForgotPassword.hasObservers()) {
            viewModel.responseForgotPassword.removeObservers(viewLifecycleOwner)
        }
        // progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        viewModel.responseForgotPassword.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        402 -> {
                            binding.etConEmailOwner.error = it.data.message
                        }
                        404 -> {
                            binding.etConEmailOwner.error = it.data.message
                        }
                        200 -> {
                            bundle.putString(EMAIL_BUNDLE, binding.etEmailOwner.text.toString())
                            findNavController().navigate(
                                R.id.action_forgotPasswordFragment_to_verifyOtpFragment,
                                bundle
                            )
                        }
                    }
                }
                ERROR -> {
                    progressDialog.dismiss()
                    AlertDialog.Builder(requireContext())
                        .setTitle("Pesan")
                        .setMessage(it.message ?: "error")
                        .setPositiveButton("Ok") { positiveButton, _ ->
                            positiveButton.dismiss()
                        }
                        .show()
                }
                LOADING -> {
                    progressDialog.show()
                }
            }

        }

    }

    private fun requestForgotPassword() {
        binding.etConEmailOwner.error = null
        val email = binding.etEmailOwner.text.toString()
        if (email.isEmpty()) {
            binding.etConEmailOwner.error = "Email tidak boleh kosong"
        } else {
            val requestEmail = RequestEmailCheck(email)
            viewModel.forgotPassword(email = requestEmail)
        }
    }
}