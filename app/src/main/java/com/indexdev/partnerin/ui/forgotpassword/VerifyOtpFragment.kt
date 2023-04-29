package com.indexdev.partnerin.ui.forgotpassword

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import com.indexdev.partnerin.data.model.request.RequestVerifyOtp
import com.indexdev.partnerin.databinding.FragmentVerifyOtpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyOtpFragment : Fragment() {
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyOtpViewModel by viewModels()

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
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        verifyObserver()
        resendCodeObserver()
        countdownTimer()
        binding.btnVerify.setOnClickListener {
            doVerify()
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_verifyOtpFragment_to_forgotPasswordFragment)
        }
        binding.btnResendCode.setOnClickListener {
            if (binding.tvTime.text.toString().toInt() > 0) {
                Toast.makeText(requireContext(), "Tunggu hingga waktu habis", Toast.LENGTH_SHORT)
                    .show()
            } else {
                doResendCode()
            }
        }
    }

    private fun countdownTimer() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinish: Long) {
                binding.tvTime.setText("${millisUntilFinish / 1000}")
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun verifyObserver() {
        // progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        viewModel.responseVerifyOtp.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        400 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage(it.data.message)
                                .setPositiveButton("Ok") { positiveButton, _ ->
                                    positiveButton.dismiss()
                                    binding.etOtpCode.text?.clear()
                                }
                                .show()
                        }
                        404 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage(it.data.message)
                                .setPositiveButton("Ok") { positiveButton, _ ->
                                    positiveButton.dismiss()
                                    binding.etOtpCode.text?.clear()
                                }
                                .show()
                        }
                        200 -> {
                            findNavController().navigate(R.id.action_verifyOtpFragment_to_newPasswordFragment)
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

    private fun doVerify() {
        val email = arguments?.getString(ForgotPasswordFragment.EMAIL_BUNDLE).toString()
        val otp = binding.etOtpCode.text.toString()
        if (otp.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Pesan")
                .setMessage("Kode verifikasi masih kosong")
                .setPositiveButton("Ok") { positiveButton, _ ->
                    positiveButton.dismiss()
                }
                .show()
        } else if (otp.length < 4) {
            AlertDialog.Builder(requireContext())
                .setTitle("Pesan")
                .setMessage("Kode verifikasi belum lengkap")
                .setPositiveButton("Ok") { positiveButton, _ ->
                    positiveButton.dismiss()
                    binding.etOtpCode.text?.clear()
                }
                .show()
        } else {
            val requestVerifyOtp = RequestVerifyOtp(email, otp.toInt())
            viewModel.verifyOtp(requestVerifyOtp)
        }
    }

    private fun resendCodeObserver() {
        // progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        viewModel.responseResendCode.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            Toast.makeText(
                                requireContext(),
                                "Kode verifikasi telah dikirim ulang.",
                                Toast.LENGTH_SHORT
                            ).show()
                            countdownTimer()
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

    private fun doResendCode() {
        val email = arguments?.getString(ForgotPasswordFragment.EMAIL_BUNDLE).toString()
        val requestEmail = RequestEmailCheck(email)
        viewModel.resendCode(email = requestEmail)
    }
}