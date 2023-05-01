package com.indexdev.partnerin.ui.home

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status
import com.indexdev.partnerin.databinding.FragmentHomeBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ID_USER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        sharedPref =
            requireContext().getSharedPreferences(LoginFragment.USER_SP, Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)


        binding.btnFabAdd.setOnClickListener {
            observeUser()
        }
    }

    private fun observeUser() {
        viewModel.getUserById(sharedPref.getString(ID_USER, DEFAULT_VALUE).toString().toInt())
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseUserById.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialog.dismiss()
                        when (it.data?.code) {
                            200 -> {
                                if (it.data.userMitraById.alamat == "" ||
                                    it.data.userMitraById.jamBuka == "" ||
                                    it.data.userMitraById.jamTutup == ""
                                ) {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Lengkapi data usaha")
                                        .setMessage("Untuk menambahkan produk silahkan lengkapi data usaha anda terlebih dahulu")
                                        .setPositiveButton("Ok") { positiveButton, _ ->
                                            findNavController().navigate(R.id.action_homeFragment_to_accountSettingsFragment)
                                            positiveButton.dismiss()
                                        }
                                        .show()
                                } else {
                                    findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
                                }
                            }
                        }

                    }
                    Status.ERROR -> {
                        progressDialog.dismiss()
                        AlertDialog.Builder(requireContext())
                            .setTitle("Pesan")
                            .setMessage(it.message ?: "error")
                            .setPositiveButton("Ok") { positiveButton, _ ->
                                positiveButton.dismiss()
                            }
                            .show()
                    }
                    Status.LOADING -> {
                        progressDialog.show()
                    }
                }
            }
        },200)

    }
}