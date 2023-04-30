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
import com.indexdev.partnerin.data.model.request.RequestNewPassword
import com.indexdev.partnerin.databinding.FragmentNewPasswordBinding
import com.indexdev.partnerin.ui.forgotpassword.ForgotPasswordFragment.Companion.EMAIL_BUNDLE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPasswordFragment : Fragment() {
    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPasswordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_newPasswordFragment_to_loginFragment)
        }
        newPasswordObserver()
        binding.btnSend.setOnClickListener {
            doPasswordChange()
        }
    }

    private fun newPasswordObserver() {
        // progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        viewModel.responseNewPassword.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    when (it.data?.code) {
                        200 -> {
                            progressDialog.dismiss()
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage("Password berhasil diubah silahkan mencoba untuk masuk kembali.")
                                .setPositiveButton("Ok") { positiveButton, _ ->
                                    positiveButton.dismiss()
                                    findNavController().navigate(R.id.action_newPasswordFragment_to_loginFragment)
                                }
                                .show()
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

    private fun doPasswordChange() {
        binding.etConConfPassword.error = null
        binding.etConPassword.error = null

        val newPassword = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfPassword.text.toString()
        val passwordRegex = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}".toRegex()

        if (newPassword.isEmpty()) {
            binding.etConPassword.error = "Kata sandi tidak boleh kosong"
        } else if (confirmPassword.isEmpty()) {
            binding.etConConfPassword.error = "Konfirmasi kata sandi tidak boleh kosong"
        } else if (!passwordRegex.matches(newPassword)) {
            binding.etConPassword.error =
                "Password harus mengandung huruf besar, kecil dan minimal 6 karakter"
        } else if (newPassword != confirmPassword) {
            binding.etConConfPassword.error = "Konfirmasi password tidak sama"
        } else {
            val email = arguments?.getString(EMAIL_BUNDLE).toString()
            val requestNewPassword = RequestNewPassword(email, newPassword)
            viewModel.newPassword(requestNewPassword)
        }
    }
}