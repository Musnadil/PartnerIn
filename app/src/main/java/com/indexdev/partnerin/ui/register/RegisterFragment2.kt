package com.indexdev.partnerin.ui.register

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.github.dhaval2404.imagepicker.ImagePicker
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.databinding.FragmentRegister2Binding
import com.indexdev.partnerin.ui.register.RegisterFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.register.RegisterFragment.Companion.OWNER
import com.indexdev.partnerin.ui.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class RegisterFragment2 : Fragment() {
    private var _binding: FragmentRegister2Binding? = null
    private val binding get() = _binding!!
    private var uri: String = ""
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegister2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences(
            RegisterFragment.REGISTER_SP,
            Context.MODE_PRIVATE
        )
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment2_to_registerFragment)
        }
        binding.ivProductPhoto.setOnClickListener {
            openImagePicker()
        }
        doRegister()
        registerObserver()
    }

    fun doRegister() {
        //get data from page register 1

        val owner = sharedPref.getString(OWNER, DEFAULT_VALUE)
        val businessName = sharedPref.getString(RegisterFragment.BUSINESS_NAME, DEFAULT_VALUE)
        val emailOwner = sharedPref.getString(RegisterFragment.EMAIL_OWNER, DEFAULT_VALUE)
        val numberPhone = sharedPref.getString(RegisterFragment.NUMBER_PHONE, DEFAULT_VALUE)
        val businessType = sharedPref.getString(RegisterFragment.BUSINESS_TYPE, DEFAULT_VALUE)
        val password = sharedPref.getString(RegisterFragment.PASSWORD, DEFAULT_VALUE)
        val lat = sharedPref.getString(RegisterFragment.LATITUDE, DEFAULT_VALUE)
        val longi = sharedPref.getString(RegisterFragment.LONGITUDE, DEFAULT_VALUE)

        binding.btnRegister.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Pesan")
                .setMessage(getString(R.string.pesan_register))
                .setNegativeButton("Batal") { negativeButton, _ ->
                    negativeButton.dismiss()
                }
                .setPositiveButton("Ok") { positiveButton, _ ->
                    viewModel.register(
                        0,
                        owner.toString(),
                        businessName.toString(),
                        emailOwner.toString(),
                        numberPhone.toString(),
                        businessType.toString(),
                        uriToFile(Uri.parse(uri), requireContext()),
                        password.toString(),
                        "",
                        "",
                        "",
                        "",
                        lat.toString().toFloat(),
                        longi.toString().toFloat(),
                        2,
                        "deactive"
                    )
                    positiveButton.dismiss()
                }
                .show()
        }
    }

    fun registerObserver() {
        // progress dialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCancelable(false)

        viewModel.responseRegister.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage(getString(R.string.pesan_sukses_register))
                                .setPositiveButton("Ok") { positiveButton, _ ->
                                    positiveButton.dismiss()
                                    findNavController().navigate(R.id.action_registerFragment2_to_loginFragment)
                                }
                                .show()
                            sharedPref.edit().clear().apply()
                        }
                        400 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage(it.data?.message ?: "error")
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
    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .crop()
            .saveDir(
                File(
                    requireContext().externalCacheDir,
                    "ImagePicker"
                )
            )
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            )
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    uri = fileUri.toString()
                    if (fileUri != null) {
                        loadImage(fileUri)
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {

                }
            }
        }

    private fun loadImage(uri: Uri) {
        binding.apply {
            Glide.with(binding.root)
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(12))
                .into(ivProductPhoto)

        }
    }
}