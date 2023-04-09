package com.indexdev.partnerin.ui.register

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.indexdev.partnerin.R
import com.indexdev.partnerin.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat = ""
    var long = ""

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 3000
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 5000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jenisUsaha = resources.getStringArray(R.array.jenis_usaha)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, jenisUsaha)
        binding.etTypeOfBusiness.setAdapter(arrayAdapter)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationSettings()
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            fetchLocation()
            Toast.makeText(requireContext(), "$lat,$long", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registerFragment_to_registerFragment2)
        }
    }

    private fun requestLocationSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder)
        fetchLocation()

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(
                        requireActivity(),100
                    )
                }catch (e:IntentSender.SendIntentException){
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
            if (it!=null){
                lat = it.latitude.toString()
                long = it.longitude.toString()
//                Log.d("lok","${it.latitude},${it.longitude}")
//                Toast.makeText(requireContext(), "${it.latitude},${it.longitude}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}