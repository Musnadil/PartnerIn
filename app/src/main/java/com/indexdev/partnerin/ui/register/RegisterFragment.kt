package com.indexdev.partnerin.ui.register

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.ApiService
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import com.indexdev.partnerin.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat = ""
    var long = ""

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 3000
        fastestInterval = 3000
        priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 5000
    }

    companion object {
        const val REGISTER_SP = "PREFERENCESREGISTER"
        const val OWNER = "OWNER"
        const val BUSINESS_NAME = "BUSINESS_NAME"
        const val EMAIL_OWNER = "EMAIL_OWNER"
        const val NUMBER_PHONE = "NUMBER_PHONE"
        const val BUSINESS_TYPE = "BUSINESS_TYPE"
        const val PASSWORD = "PASSWORD"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val DEFAULT_VALUE = "DEFAULT_VALUE"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val jenisUsaha = resources.getStringArray(R.array.jenis_usaha)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, jenisUsaha)
        binding.etTypeOfBusiness.setAdapter(arrayAdapter)
        val sharedPref = requireContext().getSharedPreferences(REGISTER_SP, Context.MODE_PRIVATE)
        val owner = sharedPref.getString(OWNER, DEFAULT_VALUE)
        if (owner != DEFAULT_VALUE) {
            binding.etBusinessOwner.setText(sharedPref.getString(OWNER, DEFAULT_VALUE))
            binding.etBusinessName.setText(sharedPref.getString(BUSINESS_NAME, DEFAULT_VALUE))
            binding.etEmailOwner.setText(sharedPref.getString(EMAIL_OWNER, DEFAULT_VALUE))
            binding.etNumberPhone.setText(sharedPref.getString(NUMBER_PHONE, DEFAULT_VALUE))
            binding.etTypeOfBusiness.setText(sharedPref.getString(BUSINESS_TYPE, DEFAULT_VALUE))
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationSettings()
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            fetchLocation()
            doRegister()
        }
        emailObserver()
    }

    private fun emailObserver() {
        if (viewModel.responseEmailCheck.hasObservers()) {
            viewModel.responseEmailCheck.removeObservers(viewLifecycleOwner)
        }
        //shared preferences
        val sharedPref = requireContext().getSharedPreferences(REGISTER_SP, Context.MODE_PRIVATE)
        val editorPrefRegister = sharedPref.edit()

        // progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)

        viewModel.responseEmailCheck.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.message) {
                        "Email sudah digunakan" -> {
                            binding.etConEmailOwner.error = it.data.message
                            binding.etEmailOwner.requestFocus()
                        }
                        "Email tidak ditemukan" -> {
                            binding.etConEmailOwner.error = it.data.message
                            binding.etEmailOwner.requestFocus()
                        }
                        "Email yang anda masukan tidak valid" -> {
                            binding.etConEmailOwner.error = it.data.message
                            binding.etEmailOwner.requestFocus()
                        }
                        "valid" -> {
                            editorPrefRegister.putString(
                                OWNER,
                                binding.etBusinessOwner.text.toString()
                            )
                            editorPrefRegister.putString(
                                BUSINESS_NAME,
                                binding.etBusinessName.text.toString()
                            )
                            editorPrefRegister.putString(
                                EMAIL_OWNER,
                                binding.etEmailOwner.text.toString()
                            )
                            editorPrefRegister.putString(
                                NUMBER_PHONE,
                                binding.etNumberPhone.text.toString()
                            )
                            editorPrefRegister.putString(
                                BUSINESS_TYPE,
                                binding.etTypeOfBusiness.text.toString()
                            )
                            editorPrefRegister.putString(
                                PASSWORD,
                                binding.etPassword.text.toString()
                            )
                            editorPrefRegister.putString(LATITUDE, lat)
                            editorPrefRegister.putString(LONGITUDE, long)
                            editorPrefRegister.apply()
                            findNavController().navigate(R.id.action_registerFragment_to_registerFragment2)
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

    private fun doRegister() {
        resetError()
        val businessOwner = binding.etBusinessOwner.text.toString()
        val businessName = binding.etBusinessName.text.toString()
        val emailOwner = binding.etEmailOwner.text.toString()
        val numberPhone = binding.etNumberPhone.text.toString()
        val businessType = binding.etTypeOfBusiness.text.toString()
        val password = binding.etPassword.text.toString()
        val confPassword = binding.etConfirmPassword.text.toString()
        val passwordRegex = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}".toRegex()

        if (businessOwner.isEmpty()) {
            binding.etConBusinessOwner.error = "Nama pemilik usaha tidak boleh kosong"
        } else if (businessOwner.length < 3) {
            binding.etConBusinessOwner.error = "Nama terlalu pendek"
        } else if (!businessOwner.matches("[a-zA-Z ]+".toRegex())) {
            binding.etConBusinessOwner.error = "Nama tidak boleh ada karakter selain huruf"
        } else if (businessName.isEmpty()) {
            binding.etConBusinessName.error = "Nama usaha tidak boleh kosong"
        } else if (emailOwner.isEmpty()) {
            binding.etConEmailOwner.error = "Email pemilik tidak boleh kosong"
        } else if (numberPhone.isEmpty()) {
            binding.etConNumberPhone.error = "Nomer ponsel tidak boleh kosong"
        } else if (businessType.isEmpty()) {
            binding.etConTypeOfBusiness.error = "Jenis usaha tidak boleh kosong"
        } else if (password.isEmpty()) {
            binding.etConPassword.error = "Password tidak boleh kosong"
        } else if (confPassword.isEmpty()) {
            binding.etConConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
        } else if (password != confPassword) {
            binding.etConConfirmPassword.error = "Konfirmasi password tidak sama"
        } else if (!passwordRegex.matches(password)) {
            binding.etConPassword.error =
                "Password harus mengandung huruf besar, kecil dan minimal 6 karakter"
        } else {
            val email = RequestEmailCheck(emailOwner)
            viewModel.emailCheck(email)
        }
    }

    private fun resetError() {
        binding.etConBusinessOwner.error = null
        binding.etConBusinessName.error = null
        binding.etConEmailOwner.error = null
        binding.etConNumberPhone.error = null
        binding.etConTypeOfBusiness.error = null
        binding.etConPassword.error = null
        binding.etConConfirmPassword.error = null
    }

    private fun requestLocationSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder)
        fetchLocation()

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(), 100
                    )
                } catch (e: IntentSender.SendIntentException) {
                }
            }
        }

    }

    private fun fetchLocation() {
        val task: Task<Location> = fusedLocationClient.lastLocation
        //request permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                lat = it.latitude.toString()
                long = it.longitude.toString()
            }
        }
    }
}