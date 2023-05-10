package com.indexdev.partnerin.ui.editmarker

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.IntentSender
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
import com.indexdev.partnerin.databinding.FragmentEditMarkerBinding
import com.indexdev.partnerin.ui.managerhome.ManagerHomeFragment
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class EditMarkerFragment : Fragment() {
    private var _binding: FragmentEditMarkerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditMarkerViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var facilityCode = ""
    var tourCode = ""
    var lat = ""
    var long = ""

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
        _binding = FragmentEditMarkerBinding.inflate(layoutInflater, container, false)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationSettings()
        observeMarker()

        binding.btnGetLocation.setOnClickListener {
            fetchLocation()
            progressDialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
                binding.etLocation.text = "Lat: $lat, Long: $long"
                binding.etLocation.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                binding.tvLocationText.visibility = View.VISIBLE
            }, 1000)

        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            validate()
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Pesan")
                .setMessage("Anda yakin ingin menghapus fasilitas?")
                .setPositiveButton("Ya") { positiveButton, _ ->
                    positiveButton.dismiss()
                    doDeleteMarker()
                }
                .setNegativeButton("Batal") { negativeButton, _ ->
                    negativeButton.dismiss()
                }
                .show()
        }
    }

    private fun doDeleteMarker() {
        progressDialog.show()
        val idMarker = arguments?.get(ManagerHomeFragment.MARKER_ID)
        viewModel.deleteMarker(idMarker.toString().toInt())
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseDeleteMarker.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        progressDialog.dismiss()
                        when (it.data?.code) {
                            200 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Fasilitas telah dihapus",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().popBackStack()
                            }
                            else -> {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Pesan")
                                    .setMessage(it.message ?: "error")
                                    .setPositiveButton("Ok") { positiveButton, _ ->
                                        positiveButton.dismiss()
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
        }, 1000)
    }

    private fun observeMarker() {
        progressDialog.show()
        val idMarker = arguments?.get(ManagerHomeFragment.MARKER_ID)
        viewModel.getMarkerById(idMarker.toString().toInt())
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseMarkerById.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        progressDialog.dismiss()
                        when (it.data?.code) {
                            200 -> {
                                facilityCode = it.data.poiById.kodeFasilitas
                                tourCode = it.data.poiById.kodeWisata
                                lat = it.data.poiById.lat
                                long = it.data.poiById.longi
                                val facilityType = when (it.data.poiById.kodeFasilitas) {
                                    "F02" -> "Toilet"
                                    "F03" -> "Food Court"
                                    "F04" -> "Titik Evakuasi"
                                    "F05" -> "Tempat Parkir"
                                    "F06" -> "Masjid"
                                    "F07" -> "Gereja"
                                    "F08" -> "Pura"
                                    "F09" -> "Vihara"
                                    "F10" -> "Klenteng"
                                    else -> ""
                                }

                                binding.etFacilityName.setText(it.data.poiById.namaFasilitas)
                                binding.etLocation.text = "Lat: $lat, Long: $long"
                                binding.etLocation.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black
                                    )
                                )
                                binding.tvLocationText.visibility = View.VISIBLE

                                binding.etFacilityType.setText(facilityType)
                                val facility = resources.getStringArray(R.array.jenis_fasilitas)
                                val arrayAdapter =
                                    ArrayAdapter(requireContext(), R.layout.dropdown_item, facility)
                                binding.etFacilityType.setAdapter(arrayAdapter)
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
                    doEditMarker(requestMarker)
                }
                .setNegativeButton("Batal") { negativeButton, _ ->
                    negativeButton.dismiss()
                }
                .show()
        }
    }

    private fun doEditMarker(requestMarker: RequestMarker) {
        progressDialog.show()
        val idMarker = arguments?.get(ManagerHomeFragment.MARKER_ID).toString().toInt()
        viewModel.editMarker(idMarker, requestMarker)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseEditMarker.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        progressDialog.dismiss()
                        when (it.data?.code) {
                            200 -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Berhasil mengubah data fasilitas",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().popBackStack()
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal mengubah data fasilitas",
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