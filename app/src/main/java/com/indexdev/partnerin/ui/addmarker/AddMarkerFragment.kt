package com.indexdev.partnerin.ui.addmarker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestMarker
import com.indexdev.partnerin.databinding.FragmentAddMarkerBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class AddMarkerFragment : Fragment() {
    private var _binding: FragmentAddMarkerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMarkerViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPref: SharedPreferences


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat = ""
    var long = ""
    var tourCode = ""

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 3000
        fastestInterval = 3000
        priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 5000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMarkerBinding.inflate(layoutInflater, container, false)
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
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Harap tunggu...")
        sharedPref =
            requireContext().getSharedPreferences(LoginFragment.USER_SP, Context.MODE_PRIVATE)

        val facility = resources.getStringArray(R.array.jenis_fasilitas)
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item, facility)
        binding.etFacilityType.setAdapter(arrayAdapter)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationSettings()

        binding.btnGetLocation.setOnClickListener {
            fetchLocation()
            binding.etLocation.text = "Lat: $lat, Long: $long"
            binding.etLocation.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvLocationText.visibility = View.VISIBLE
        }
        binding.btnAdd.setOnClickListener {
            validate()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        observeUser()
    }

    private fun validate() {
        val facilityName = binding.etFacilityName.text.toString()
        val facilityLocation = binding.etLocation.text.toString()
        val facilityType = binding.etFacilityType.text.toString()

        binding.etFacilityNameContainer.error = null
        binding.etFacilityTypeContainer.error = null
        binding.tvErrorLocation.text = ""

        if (facilityName.isEmpty()) {
            binding.etFacilityNameContainer.error = "Nama fasilitas tidak boleh kosong"
        } else if (facilityName.length < 3) {
            binding.etFacilityNameContainer.error = "Nama fasilitas terlalu singkat"
        } else if (facilityLocation == "Lokasi") {
            binding.tvErrorLocation.text = "Lokasi fasilitas tidak boleh kosong"
        } else if (facilityType.isEmpty()) {
            binding.etFacilityTypeContainer.error = "Jenis fasilitas tidak boleh kosong"
        } else {
            val facilityCode = when (facilityType) {
                "Toilet" -> "F02"
                "Food Court" -> "F03"
                "Titik Evakuasi" -> "F04"
                "Tempat Parkir" -> "F05"
                "Masjid" -> "F06"
                "Gereja" -> "F07"
                "Pura" -> "F08"
                "Vihara" -> "F09"
                "Klenteng" -> "F10"
                else -> ""
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Peringatan")
                .setMessage(getString(R.string.pesan_konfirmasi_tambah_marker))
                .setPositiveButton("Ok") { positiveButton, _ ->
                    positiveButton.dismiss()
                    val requestMarker =
                        RequestMarker(facilityCode, tourCode, lat, long, facilityName)
                    doAddMarker(requestMarker)
                }
                .setNegativeButton("Batal") { negativeButton, _ ->
                    negativeButton.dismiss()
                }
                .show()
        }
    }

    private fun doAddMarker(requestMarker: RequestMarker) {
        progressDialog.show()
        viewModel.addMarker(requestMarker)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseAddMarker.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        progressDialog.dismiss()
                        when (it.data?.code) {
                            200 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Berhasil menambahkan fasilitas",
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
        }, 1000)
    }

    private fun observeUser() {
        viewModel.getUserById(
            sharedPref.getString(
                LoginFragment.ID_USER,
                LoginFragment.DEFAULT_VALUE
            ).toString().toInt()
        )
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseUserById.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        progressDialog.dismiss()
                        when (it.data?.code) {
                            200 -> {
                                if (it.data.userMitraById.kodeWisata != "0") {
                                    tourCode = it.data.userMitraById.kodeWisata
                                } else {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Pesan")
                                        .setMessage("Kode wisata tidak valid")
                                        .setPositiveButton("Ok") { positiveButton, _ ->
                                            positiveButton.dismiss()
                                        }
                                        .show()
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
        }, 200)
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