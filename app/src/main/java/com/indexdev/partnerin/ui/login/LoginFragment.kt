package com.indexdev.partnerin.ui.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestLogin
import com.indexdev.partnerin.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPref: SharedPreferences

    companion object {
        const val USER_SP = "USER_SP"
        const val ID_USER = "ID_USER"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        sharedPref = requireContext().getSharedPreferences(USER_SP, Context.MODE_PRIVATE)

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            doLogin()
        }
        loginObserver()
    }

    private fun doLogin() {
        binding.etConEmailOwner.error = null
        binding.etConPassword.error = null

        if (binding.etEmailOwner.text.toString().isEmpty()) {
            binding.etConEmailOwner.error = "Email tidak boleh kosong"
        } else if (binding.etPassword.text.toString().isEmpty()) {
            binding.etConPassword.error = "Password tidak boleh kosong"
        } else {
            val requestLogin = RequestLogin(
                binding.etEmailOwner.text.toString(),
                binding.etPassword.text.toString()
            )
            viewModel.login(requestLogin)
        }
    }

    private fun loginObserver() {
        viewModel.responseLogin.observe(viewLifecycleOwner) {
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
                        405 -> {
                            binding.etConPassword.error = it.data.message
                        }
                        406 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage(it.data.message ?: "error")
                                .setPositiveButton("Ok") { positiveButton, _ ->
                                    positiveButton.dismiss()
                                }
                                .show()
                        }
                        200 -> {
                            sharedPref.edit().putString(ID_USER, it.data.idMitra)
                                .apply()
                            when (it.data.role) {
                                "0" -> {
                                    findNavController().navigate(R.id.action_loginFragment_to_superAdminHomeFragment)
                                }
                                "1" -> {
                                    findNavController().navigate(R.id.action_loginFragment_to_managerHomeFragment)
                                }
                                "2" -> {
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                }
                            }
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

}