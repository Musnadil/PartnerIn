@file:Suppress("DEPRECATION")

package com.indexdev.partnerin

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.indexdev.partnerin.databinding.ActivityMainBinding
import com.indexdev.partnerin.ui.lightStatusBar
import com.indexdev.partnerin.ui.setFullScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listFragment = listOf(
        R.id.splashScreenFragment,
        R.id.registerFragment,
        R.id.loginFragment,
        R.id.registerFragment2,
        R.id.forgotPasswordFragment,
        R.id.verifyOtpFragment,
        R.id.newPasswordFragment,
    )
    private val listFragmentNoMenu = listOf(
        R.id.addProductFragment,
        R.id.editProductFragment,
        R.id.editAccountFragment,
        R.id.accountSettingsFragment,
        R.id.superAdminHomeFragment,
        R.id.superAdminApprovalFragment,
        R.id.superAdminDisableFragment,

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        lightStatusBar(window)
        setFullScreen(window)

        val navController = findNavController(R.id.fragmentContainerView)
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                in listFragment -> {
                    hideSystemUI()
                    binding.bottomNavigation.visibility = View.GONE
                }
                in listFragmentNoMenu -> {
                    lightStatusBar(window)
                    showSystemUI()
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    showSystemUI()
                    lightStatusBar(window)
                }
            }
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }
}