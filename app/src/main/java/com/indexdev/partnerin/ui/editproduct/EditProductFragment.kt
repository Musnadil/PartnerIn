package com.indexdev.partnerin.ui.editproduct

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.indexdev.partnerin.databinding.FragmentEditProductBinding
import com.indexdev.partnerin.ui.home.HomeFragment.Companion.ID_PRODUK
import com.indexdev.partnerin.ui.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditProductFragment : Fragment() {
    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    private var uri: String = ""
    private val viewModel: EditProductViewModel by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private var idProduct = 0
    private var idMitra = 0
    private var oldImage = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(layoutInflater, container, false)
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
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Harap tunggu...")
        progressDialog.setCancelable(false)
        val satuan = resources.getStringArray(R.array.satuan_harga)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, satuan)
        binding.etDropdownUnit.setAdapter(arrayAdapter)
        binding.ivProductPhoto.setOnClickListener {
            openImagePicker()
        }
        getProductById()
        updateProductObserver()
        deleteProductObserver()
        binding.btnSave.setOnClickListener {
            doUpdateProduct()
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Pesan")
                .setMessage("Anda yakin ingin menghapus ${binding.etProductName.text} dari produk anda?")
                .setPositiveButton("Ya") { positiveButton, _ ->
                    progressDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.deleteProduct(idProduct)
                    },2000)
                    positiveButton.dismiss()
                }
                .setNegativeButton("Tidak"){ negativeButton, _ ->
                    negativeButton.dismiss()
                }
                .show()
        }
    }

    private fun deleteProductObserver() {
        viewModel.responseDeleteProduct.observe(viewLifecycleOwner){
            when (it.status){
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code){
                        200 -> {
                            Toast.makeText(requireContext(), "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                }
                ERROR -> {
                    progressDialog.dismiss()
                    AlertDialog.Builder(requireContext())
                        .setTitle("Pesan")
                        .setMessage(it?.message ?: "Error")
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

    private fun updateProductObserver() {
        viewModel.responseEditProduct.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            Toast.makeText(
                                requireContext(),
                                "Berhasil mengubah produk",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }
                        400 -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage(it.data.message ?: "Error")
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
                        .setMessage(it?.message ?: "Error")
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

    private fun doUpdateProduct() {
        val productName = binding.etProductName.text.toString()
        val price = binding.etPrice.getNumericValue().toInt().toString()
        val unit = binding.etDropdownUnit.text.toString()
        val desc = binding.etDescription.text.toString()
        var file: File? = null
        if (uri.isNotEmpty()) {
            file = uriToFile(Uri.parse(uri), requireContext())
        }
        if (productName.isEmpty()) {
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
            viewModel.editProduct(
                idProduct = idProduct,
                idMitra = idMitra,
                namaProduk = productName,
                harga = price,
                satuan = unit,
                deskripsi = desc,
                gambar = file,
                gambarLama = oldImage
            )
        }
    }

    private fun getProductById() {
        arguments?.getString(ID_PRODUK)?.let { viewModel.getProductById(it.toInt()) }
        viewModel.responseProductById.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    if (it.data?.idProduk != null) {
                        idProduct = it.data.idProduk.toInt()
                        idMitra = it.data.idMitra.toInt()
                        oldImage = it.data.gambar
                        Glide.with(requireContext())
                            .load("http://192.168.206.15:8080/gambar/${it.data.gambar}")
                            .transform(CenterCrop())
                            .into(binding.ivProductPhoto)
                        binding.etProductName.setText(it.data.namaProduk)
                        binding.etPrice.setText(it.data.harga)
                        binding.etDropdownUnit.setText(it.data.satuan)
                        binding.etDescription.setText(it.data.deskripsi)
                        val satuan = resources.getStringArray(R.array.satuan_harga)
                        val arrayAdapter =
                            ArrayAdapter(requireContext(), R.layout.dropdown_item, satuan)
                        binding.etDropdownUnit.setAdapter(arrayAdapter)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Produk tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ERROR -> {
                    progressDialog.dismiss()
                    AlertDialog.Builder(requireContext())
                        .setTitle("Pesan")
                        .setMessage(it.message ?: "Error")
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