package com.indexdev.partnerin.ui.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.databinding.FragmentProfileBinding
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ID_USER
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.USER_SP
import com.indexdev.partnerin.ui.register.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog


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
        val sharedPrefUser = requireContext().getSharedPreferences(RegisterFragment.REGISTER_SP, Context.MODE_PRIVATE)
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Anda yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya") { positive, _ ->
                    positive.dismiss()
                    sharedPref.edit().clear().apply()
                    sharedPrefUser.edit().clear().apply()
                    activity?.finish()
                    activity?.startActivity(activity?.intent)
                }
                .setNegativeButton("Tidak") { negative, _ ->
                    negative.dismiss()
                }
                .show()
        }
        sharedPref = requireContext().getSharedPreferences(USER_SP, Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        getUserById()
    }

    private fun getUserById() {
        viewModel.getUserById(sharedPref.getString(ID_USER, DEFAULT_VALUE).toString().toInt())
        viewModel.responseUserById.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            binding.tvBusinessName.text = it.data.userMitraById.namaUsaha
                            binding.tvNumberPhone.text = it.data.userMitraById.noPonsel
                            binding.tvEmail.text = it.data.userMitraById.emailPemilik
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