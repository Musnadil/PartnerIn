package com.indexdev.partnerin.ui.addproduct

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
import android.widget.ArrayAdapter
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
import com.indexdev.partnerin.databinding.FragmentAddProductBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.DEFAULT_VALUE
import com.indexdev.partnerin.ui.login.LoginFragment.Companion.ID_USER
import com.indexdev.partnerin.ui.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private var uri: String = ""
    private val viewModel: AddProductViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(layoutInflater, container, false)
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
        val satuan = resources.getStringArray(R.array.satuan_harga)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, satuan)
        binding.etDropdownUnit.setAdapter(arrayAdapter)
        binding.ivProductPhoto.setOnClickListener {
            openImagePicker()
        }
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCancelable(false)
        sharedPref =
            requireContext().getSharedPreferences(LoginFragment.USER_SP, Context.MODE_PRIVATE)
        binding.btnAdd.setOnClickListener {
            addProduct()
        }
        addProductObserver()
    }

    private fun addProductObserver() {
        viewModel.responseAddProduct.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            Toast.makeText(
                                requireContext(),
                                "Produk berhasil di tambahkan",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().popBackStack()
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

    private fun addProduct() {
        binding.etProductNameContainer.error = null
        binding.etPriceContainer.error = null
        binding.etUnitContainer.error = null
        binding.etDescriptionContainer.error = null

        val productName = binding.etProductName.text.toString()
        val price = binding.etPrice.getNumericValue().toInt().toString()
        val unit = binding.etDropdownUnit.text.toString()
        val desc = binding.etDescription.text.toString()

        if (uri.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Pesan")
                .setMessage("Foto produk belum diunggah.")
                .setPositiveButton("Ok") { positiveButton, _ ->
                    positiveButton.dismiss()
                }
                .show()
        } else if (productName.isEmpty()) {
            binding.etProductNameContainer.error = "Nama produk tidak boleh kosong"
        } else if (productName.length < 5) {
            binding.etProductNameContainer.error = "Nama produk terlalu singkat"
        } else if (price.isEmpty()) {
            binding.etPriceContainer.error = "Harga produk tidak boleh kosong"
        } else if (price.toInt() < 1000) {
            binding.etPriceContainer.error = "Harga produk minimal Rp. 1000"
        } else if (unit.isEmpty()) {
            binding.etUnitContainer.error = "Satuan tidak boleh kosong"
        } else if (desc.isEmpty()) {
            binding.etDescriptionContainer.error = "Deskripsi tidak boleh kosong"
        } else if (desc.length < 30) {
            binding.etDescriptionContainer.error = "Deskripsi produk minimal 30 karakter"
        } else {
            val idMitra = sharedPref.getString(ID_USER, DEFAULT_VALUE).toString().toInt()
            viewModel.addProduct(
                idMitra, productName, price, unit, desc, uriToFile(Uri.parse(uri), requireContext())
            )
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