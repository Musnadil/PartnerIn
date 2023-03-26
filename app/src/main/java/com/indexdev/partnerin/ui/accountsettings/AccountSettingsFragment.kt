package com.indexdev.partnerin.ui.accountsettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.indexdev.partnerin.databinding.FragmentAccountSettingsBinding

class AccountSettingsFragment : Fragment() {
    private var _binding: FragmentAccountSettingsBinding? = null
    private val binding get() = _binding!!
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
}