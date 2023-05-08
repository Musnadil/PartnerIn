package com.indexdev.partnerin.ui.superadminapproval

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.request.RequestEditAccount
import com.indexdev.partnerin.databinding.FragmentSuperAdminApprovalBinding
import com.indexdev.partnerin.ui.superadminhome.SuperAdminHomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuperAdminApprovalFragment : Fragment() {
    private var _binding: FragmentSuperAdminApprovalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SuperAdminApprovalViewModel by viewModels()
    private var partnerId = 0
    private lateinit var progressDialog: ProgressDialog

    var kodeWisata = ""
    var namaUsaha = ""
    var emailPemilik = ""
    var noPonsel = ""
    var alamat = ""
    var hariBuka = ""
    var jamBuka = ""
    var jamTutup = ""
    var status = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuperAdminApprovalBinding.inflate(layoutInflater, container, false)
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

        partnerId = arguments?.get(SuperAdminHomeFragment.PARTNER_ID).toString().toInt()

        getUser()
        approvePartnerObserver()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnApprove.setOnClickListener {
            doApprovePartner()
        }
    }

    private fun approvePartnerObserver() {
        viewModel.responseApproval.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            Toast.makeText(
                                requireContext(),
                                "Status akun $namaUsaha telah diubah",
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
    }

    private fun doApprovePartner() {
        val partnerId = arguments?.get(SuperAdminHomeFragment.PARTNER_ID).toString().toInt()
        val newStatus = when (status) {
            "deactive" -> {
                "active"
            }
            "active" -> {
                "deactive"
            }
            else -> {
                ""
            }
        }
        val requestEditAccount = RequestEditAccount(
            kodeWisata = kodeWisata.toInt(),
            namaUsaha = namaUsaha,
            emailPemilik = emailPemilik,
            noPonsel = noPonsel,
            alamat = alamat,
            hariBuka = hariBuka,
            jamBuka = jamBuka,
            jamTutup = jamTutup,
            status = newStatus
        )
        viewModel.approvePartner(partnerId, requestEditAccount)
    }

    @SuppressLint("SetTextI18n")
    private fun getUser() {
        viewModel.getUserById(partnerId)
        viewModel.responseUserById.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    progressDialog.dismiss()
                    when (it.data?.code) {
                        200 -> {
                            kodeWisata = it.data.userMitraById.kodeWisata
                            namaUsaha = it.data.userMitraById.namaUsaha
                            emailPemilik = it.data.userMitraById.emailPemilik
                            noPonsel = it.data.userMitraById.noPonsel
                            alamat = it.data.userMitraById.alamat
                            hariBuka = it.data.userMitraById.hariBuka
                            jamBuka = it.data.userMitraById.jamBuka
                            jamTutup = it.data.userMitraById.jamTutup
                            status = it.data.userMitraById.status

                            if (status == "active") {
                                binding.btnApprove.setText("Nonaktifkan")
                                binding.btnApprove.setBackgroundColor(Color.parseColor("#DF2E38"))
                            }

                            Glide.with(requireContext())
                                .load("http://192.168.0.107:8080/gambar/${it.data.userMitraById.gambar}")
                                .transform(CenterCrop())
                                .into(binding.ivBusiness)
                            val lat = it.data.userMitraById.lat
                            val longi = it.data.userMitraById.longi

                            binding.tvBusinessName.text = "Nama Usaha : $namaUsaha"
                            binding.tvBusinessOwner.text =
                                "Nama Pemilik : ${it.data.userMitraById.namaPemilik}"
                            binding.tvEmail.text = "Email Pemilik : $emailPemilik"
                            binding.tvNumberPhone.text = "Nomor Ponsel : $noPonsel"
                            binding.tvBusinessType.text =
                                "Jenis Usaha : ${it.data.userMitraById.jenisUsaha}"
                            binding.btnVisit.setOnClickListener {
                                val googleMapsUrl =
                                    "https://www.google.com/maps?q=${lat.toDouble()},${
                                        longi.toDouble()
                                    }"
                                val uri = Uri.parse(googleMapsUrl)
                                val googleMapsPackage = "com.google.android.apps.maps"
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage(googleMapsPackage)
                                }
                                startActivity(intent)
                            }
                        }
                        else -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Pesan")
                                .setMessage("Pengguna tidak ditemukan")
                                .setPositiveButton("Ok") { positiveButton, _ ->
                                    positiveButton.dismiss()
                                    findNavController().popBackStack()
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
}