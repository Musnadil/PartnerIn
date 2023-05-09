package com.indexdev.partnerin.ui.managerhome

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.indexdev.partnerin.R
import com.indexdev.partnerin.data.api.Status
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.response.ResponseGetAllUserPartnerItem
import com.indexdev.partnerin.data.model.response.ResponseGetMarkerItem
import com.indexdev.partnerin.databinding.FragmentManagerHomeBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import com.indexdev.partnerin.ui.register.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagerHomeFragment : Fragment() {
    private var _binding: FragmentManagerHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: ProgressDialog
    private val viewModel: ManagerHomeViewModel by viewModels()
    private val listMarker: MutableList<ResponseGetMarkerItem> = ArrayList()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var markerAdapter: MarkerAdapter

    companion object {
        const val MARKER_ID = "MARKER_ID"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagerHomeBinding.inflate(layoutInflater, container, false)
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
        binding.btnFabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_managerHomeFragment_to_addMarkerFragment)
        }
        sharedPref =
            requireContext().getSharedPreferences(LoginFragment.USER_SP, Context.MODE_PRIVATE)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Harap tunggu...")

        detailMarker()
        observeUser()
        markerObserver()
        logout()
    }

    private fun setTourTitle(idTour: Int) {
        binding.tvFacility.text = when (idTour) {
            101 -> "Fasilitas Danau Toba"
            102 -> "Fasilitas Pantai Tanjung Kelayang"
            103 -> "Fasilitas Pantai Tanjung Lesung"
            104 -> "Fasilitas Pulau Seribu"
            105 -> "Fasilitas Candi Borobudur"
            106 -> "Fasilitas Taman Nasional Bromo Tengger Semeru"
            107 -> "Fasilitas Pantai Kuta Mandalika"
            108 -> "Fasilitas Labuan Bajo"
            109 -> "Fasilitas Wakatobi"
            110 -> "Fasilitas Morotai"
            else -> ""
        }
    }

    private fun detailMarker() {
        val bundle = Bundle()
        markerAdapter = MarkerAdapter(object : MarkerAdapter.OnClickListener {
            override fun onClickItem(data: ResponseGetMarkerItem) {
                bundle.putString(MARKER_ID, data.idPoi)
                findNavController()
                    .navigate(R.id.action_managerHomeFragment_to_editMarkerFragment, bundle)
            }
        })
        binding.rvProduct.adapter = markerAdapter
    }

    private fun markerObserver() {
        progressDialog.show()
        binding.rvProduct.visibility = View.GONE
        viewModel.responseGetMarker.removeObservers(viewLifecycleOwner)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseGetMarker.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        when (it.data?.code()) {
                            200 -> {
                                if (it.data.body() != null) {
                                    listMarker.clear()
                                    listMarker.addAll(it.data.body()!!)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        progressDialog.dismiss()
                                        binding.rvProduct.visibility = View.VISIBLE
                                        markerAdapter.submitData(listMarker)
                                    }, 1000)
                                    viewModel.responseGetMarker.removeObservers(
                                        viewLifecycleOwner
                                    )
                                } else {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Pesan")
                                        .setMessage("Marker fasilitas belum ditambahkan.")
                                        .setPositiveButton("Ok") { positiveButton, _ ->
                                            positiveButton.dismiss()
                                        }
                                        .show()
                                }
                            }
                            404 -> {
                                viewModel.responseGetMarker.removeObservers(
                                    viewLifecycleOwner
                                )
                                progressDialog.dismiss()
                                binding.rvProduct.visibility = View.VISIBLE
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Pesan")
                                    .setMessage("Kode fasilitas tidak terdaftar")
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

    private fun getMarkerTour(idTour: Int) {
        viewModel.getMarkerByIdTour(idTour)
    }

    private fun observeUser() {
        viewModel.getUserById(
            sharedPref.getString(
                LoginFragment.ID_USER,
                LoginFragment.DEFAULT_VALUE
            ).toString().toInt()
        )
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.responseUserById.observe(viewLifecycleOwner) {
                when (it.status) {
                    SUCCESS -> {
                        when (it.data?.code) {
                            200 -> {
                                if (it.data.userMitraById.kodeWisata != "") {
                                    getMarkerTour(it.data.userMitraById.kodeWisata.toInt())
                                    setTourTitle(it.data.userMitraById.kodeWisata.toInt())
                                }
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
        }, 200)
    }
    private fun logout() {
        val sharedPrefUser = requireContext().getSharedPreferences(
            RegisterFragment.REGISTER_SP,
            Context.MODE_PRIVATE
        )
        val sharedPref =
            requireContext().getSharedPreferences(LoginFragment.USER_SP, Context.MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Anda yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya") { positive, _ ->
                    positive.dismiss()
                    sharedPref.edit().clear().apply()
                    sharedPrefUser.edit().clear().apply()
                    activity?.finish()
                    activity?.startActivity(activity?.intent)
                }
                .setNegativeButton("Tidak") { negative, _ ->
                    negative.dismiss()
                }
                .show()
        }
    }
}