package com.indexdev.partnerin.ui.splashscreen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.databinding.FragmentSplashScreenBinding
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ID_USER
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ROLE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.USER_SP
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences(USER_SP,Context.MODE_PRIVATE)
        val role = sharedPref.getString(ROLE, DEFAULT_VALUE)
        Handler(Looper.getMainLooper()).postDelayed({
            when(role){
                "0" -> {
                    findNavController().navigate(R.id.action_splashScreenFragment_to_superAdminHomeFragment2)
                }
                "1" -> {
                    findNavController().navigate(R.id.action_splashScreenFragment_to_managerHomeFragment2)
                }
                "2" -> {
                    findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment2)
                }
                else -> {
                    findNavController().navigate(R.id.action_splashScreenFragment_to_registerFragment)
                }
            }
        }, 3000)
    }
}