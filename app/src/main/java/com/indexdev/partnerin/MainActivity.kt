package com.indexdev.partnerin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.indexdev.partnerin.databinding.ActivityMainBinding
import com.indexdev.partnerin.ui.lightStatusBar
import com.indexdev.partnerin.ui.setFullScreen

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listFragment = listOf(
        R.id.splashScreenFragment,
        R.id.registerFragment,
        R.id.loginFragment,
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
            if (destination.id in listFragment) {
                hideSystemUI()
                binding.bottomNavigation.visibility = View.GONE
            } else {
                showSystemUI()
                lightStatusBar(window)
            }
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}