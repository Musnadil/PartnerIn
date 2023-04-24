@file:Suppress("DEPRECATION")

package com.indexdev.partnerin.ui.accountsettings

import android.annotation.SuppressLint
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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.indexdev.partnerin.data.api.Status
import com.indexdev.partnerin.databinding.FragmentAccountSettingsBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSettingsFragment : Fragment() {
    private var _binding: FragmentAccountSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountSettingsViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    private var kodeWisata = ""
    private var namaUsaha = ""
    private var emailPemilik = ""
    private var noPonsel = ""
    private var alamat = ""
    private var hariBuka = ""
    private var jamBuka = ""
    private var jamTutup = ""
    private var status = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSettingsBinding.inflate(layoutInflater, container, false)
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
        sharedPref =
            requireContext().getSharedPreferences(LoginFragment.USER_SP, Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Harap tunggu...")

        getUserById()
        userObserver()
//        updateAccount()
//        responseEditObserver()
        setupDatePicker()
    }

    @SuppressLint("SetTextI18n")
    private fun setupDatePicker() {
        binding.etOpeningHours.setOnClickListener {
            val timePicker: MaterialTimePicker = MaterialTimePicker.Builder()
                .setTitleText("Tentukan Jam Buka")
                .setHour(0)
                .setMinute(0)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.show(parentFragmentManager, "timepicker")
            timePicker.addOnPositiveButtonClickListener {
                val pickedHour: Int = timePicker.hour
                val pickedMinute: Int = timePicker.minute
                if (pickedHour < 10) {
                    if (pickedMinute < 10) {
                        binding.etOpeningHours.text = "0${pickedHour}:0${pickedMinute}"
                    } else {
                        binding.etOpeningHours.text = "0${pickedHour}:${pickedMinute}"
                    }
                } else if (pickedHour > 10) {
                    if (pickedMinute < 10) {
                        binding.etOpeningHours.text = "${pickedHour}:0${pickedMinute}"
                    } else {
                        binding.etOpeningHours.text = "${pickedHour}:${pickedMinute}"
                    }
                } else {
                    binding.etOpeningHours.text = "${pickedHour}:${pickedMinute}"
                }

            }
        }
        binding.etClosingHours.setOnClickListener {
            val timePicker: MaterialTimePicker = MaterialTimePicker.Builder()
                .setTitleText("Tentukan Jam Tutup")
                .setHour(0)
                .setMinute(0)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.show(parentFragmentManager, "timepicker")
            timePicker.addOnPositiveButtonClickListener {
                val pickedHour: Int = timePicker.hour
                val pickedMinute: Int = timePicker.minute
                if (pickedHour < 10) {
                    if (pickedMinute < 10) {
                        binding.etClosingHours.text = "0${pickedHour}:0${pickedMinute}"
                    } else {
                        binding.etClosingHours.text = "0${pickedHour}:${pickedMinute}"
                    }
                } else if (pickedHour > 10) {
                    if (pickedMinute < 10) {
                        binding.etClosingHours.text = "${pickedHour}:0${pickedMinute}"
                    } else {
                        binding.etClosingHours.text = "${pickedHour}:${pickedMinute}"
                    }
                } else {
                    binding.etClosingHours.text = "${pickedHour}:${pickedMinute}"
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

                            binding.etOpeningHours.text = it.data.userMitraById.jamBuka
                            binding.etClosingHours.text = it.data.userMitraById.jamTutup
                            binding.etAddress.setText(it.data.userMitraById.alamat)

                            if (hariBuka.isNotEmpty()) {
                                val arrayOfDay = hariBuka.split(",")
                                for (i in arrayOfDay) {
                                    when (i) {
                                        "senin" -> {
                                            binding.cbMonday.isChecked = true
                                        }
                                        "selasa" -> {
                                            binding.cbTuesday.isChecked = true
                                        }
                                        "rabu" -> {
                                            binding.cbWednesday.isChecked = true
                                        }
                                        "kamis" -> {
                                            binding.cbThursday.isChecked = true
                                        }
                                        "jumat" -> {
                                            binding.cbFriday.isChecked = true
                                        }
                                        "sabtu" -> {
                                            binding.cbSaturday.isChecked = true
                                        }
                                        "minggu" -> {
                                            binding.cbSunday.isChecked = true
                                        }
                                    }
                                }
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
    }

}