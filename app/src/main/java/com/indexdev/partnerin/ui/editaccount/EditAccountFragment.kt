package com.indexdev.partnerin.ui.editaccount

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.data.api.Status
import com.indexdev.partnerin.data.model.request.RequestEditAccount
import com.indexdev.partnerin.databinding.FragmentEditAccountBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ID_USER
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.USER_SP
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountFragment : Fragment() {
    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditAccountViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    var kodeWisata = ""
    var namaUsaha = ""
    var emailPemilik = ""
    var noPonsel = ""
    var alamat = ""
    var hariBuka = ""
    var jamBuka = ""
    var jamTutup = ""
    var status = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAccountBinding.inflate(layoutInflater, container, false)
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
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        sharedPref = requireContext().getSharedPreferences(USER_SP, Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Harap tunggu...")

        getUserById()
        userObserver()
        updateAccount()
        responseEditObserver()
    }

    private fun updateAccount() {
        val idUser = sharedPref.getString(ID_USER, DEFAULT_VALUE)
        binding.btnSave.setOnClickListener {
            binding.etConBusinessName.error = null
            binding.etConEmailOwner.error = null
            binding.etConNumberPhone.error = null

            val email = binding.etEmailOwner.text.toString()
            val businessName = binding.etBusinessName.text.toString()
            val numberPhone = binding.etNumberPhone.text.toString()

            if (businessName.isEmpty()) {
                binding.etConBusinessName.error = "Nama usaha tidak boleh kosong"
            } else if (email.isEmpty()) {
                binding.etConEmailOwner.error = "Email pemilik tidak boleh kosong"
            } else if (numberPhone.isEmpty()) {
                binding.etConNumberPhone.error = "Nomer ponsel tidak boleh kosong"
            } else {
                val requestEditAccount = RequestEditAccount(
                    kodeWisata.toInt(),
                    businessName,
                    email,
                    numberPhone,
                    alamat,
                    hariBuka,
                    jamBuka,
                    jamTutup,
                    status
                )
                viewModel.editAccount(
                    idUser.toString().toInt(),
                    requestEditAccount
                )
            }
        }
    }

    private fun responseEditObserver() {
        viewModel.responseEditAccount.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        400 -> {
                            binding.etConEmailOwner.error = it.data.message
                        }
                        401 -> {
                            binding.etConEmailOwner.error = it.data.message
                        }
                        402 -> {
                            binding.etConEmailOwner.error = it.data.message
                        }
                        200 -> {
                            Toast.makeText(
                                requireContext(),
                                "Berhasil mengubah akun",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
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
    }

    private fun userObserver() {
        viewModel.responseUserById.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            kodeWisata = it.data.userMitraById.kodeWisata
                            namaUsaha = it.data.userMitraById.namaUsaha
                            emailPemilik = it.data.userMitraById.emailPemilik
                            noPonsel = it.data.userMitraById.noPonsel
                            alamat = it.data.userMitraById.alamat
                            hariBuka = it.data.userMitraById.hariBuka
                            jamBuka = it.data.userMitraById.jamBuka
                            jamTutup = it.data.userMitraById.jamTutup
                            status = it.data.userMitraById.status

                            binding.etBusinessName.setText(it.data.userMitraById.namaUsaha)
                            binding.etEmailOwner.setText(it.data.userMitraById.emailPemilik)
                            binding.etNumberPhone.setText(it.data.userMitraById.noPonsel)
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
    }

    private fun getUserById() {
        viewModel.getUserById(
            sharedPref.getString(
                LoginFragment.ID_USER,
                LoginFragment.DEFAULT_VALUE
            ).toString().toInt()
        )
    }
}