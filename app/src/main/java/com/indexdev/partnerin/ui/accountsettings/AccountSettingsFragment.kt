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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestEditAccount
import com.indexdev.partnerin.databinding.FragmentAccountSettingsBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ID_USER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSettingsFragment : Fragment() {
    private var _binding: FragmentAccountSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountSettingsViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private val arrayOfDay: MutableList<String> = ArrayList()

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

        userObserver()
        getUserById()
        updateAccountObserver()
        setupDatePicker()

        binding.btnSave.setOnClickListener {
            updateAccount()
        }
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
        viewModel.getUserById(sharedPref.getString(ID_USER, DEFAULT_VALUE).toString().toInt())
    }

    private fun userObserver() {
        viewModel.responseUserById.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
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

    private fun updateAccount() {
        val address = binding.etAddress.text.toString()
        if (binding.cbSunday.isChecked) arrayOfDay.add("minggu")
        if (binding.cbMonday.isChecked) arrayOfDay.add("senin")
        if (binding.cbTuesday.isChecked) arrayOfDay.add("selasa")
        if (binding.cbWednesday.isChecked) arrayOfDay.add("rabu")
        if (binding.cbThursday.isChecked) arrayOfDay.add("kamis")
        if (binding.cbFriday.isChecked) arrayOfDay.add("jumat")
        if (binding.cbSaturday.isChecked) arrayOfDay.add("sabtu")

        val dayOpen = arrayOfDay.joinToString(",")
        val openingHours = binding.etOpeningHours.text.toString()
        val closingHours = binding.etClosingHours.text.toString()
        binding.etConAddress.error = null
        binding.tvErrorOpeningHours.text = null
        binding.tvErrorClosingHours.text = null

        if (address.isEmpty()) {
            binding.etConAddress.error = "Alamat tidak boleh kosong"
        } else if (address.length < 15) {
            binding.etConAddress.error = "Alamat terlalu singkat"
        } else if (openingHours.isEmpty()) {
            binding.tvErrorOpeningHours.text = "Jam buka tidak boleh kosong"
        } else if (closingHours.isEmpty()) {
            binding.tvErrorClosingHours.text = "Jam tutup tidak boleh kosong"
        } else {
            val requestEditAccount = RequestEditAccount(
                kodeWisata.toInt(),
                namaUsaha,
                emailPemilik,
                noPonsel,
                address,
                dayOpen,
                openingHours,
                closingHours,
                status
            )
            val idUser = sharedPref.getString(ID_USER, DEFAULT_VALUE).toString().toInt()
            viewModel.editAccount(idUser, requestEditAccount)
        }
    }

    private fun updateAccountObserver() {
        viewModel.responseEditAccount.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            Toast.makeText(
                                requireContext(),
                                "Pengaturan akun berhasil diubah",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
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