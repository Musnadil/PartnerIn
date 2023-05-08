package com.indexdev.partnerin.ui.superadminhome

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.indexdev.partnerin.data.api.Status.*
import com.indexdev.partnerin.data.model.response.ResponseGetAllUserPartnerItem
import com.indexdev.partnerin.databinding.FragmentSuperAdminHomeBinding
import com.indexdev.partnerin.ui.login.LoginFragment
import com.indexdev.partnerin.ui.register.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuperAdminHomeFragment : Fragment() {
    private var _binding: FragmentSuperAdminHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SuperAdminHomeViewModel by viewModels()
    private val listApproved: MutableList<ResponseGetAllUserPartnerItem> = ArrayList()
    private val listWaiting: MutableList<ResponseGetAllUserPartnerItem> = ArrayList()
    private val listUser: MutableList<ResponseGetAllUserPartnerItem> = ArrayList()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var userAdapter: UserAdapter

    companion object {
        const val PARTNER_ID = "PARTNER_ID"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuperAdminHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeight > 0) {
            binding.statusbar.layoutParams.height = resources.getDimensionPixelSize(statusBarHeight)
        }
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Harap tunggu...")

        setupButton()
        getUserPartner()
        detailUser()
        logout()


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

    private fun detailUser() {
        userAdapter = UserAdapter(object : UserAdapter.OnClickListener {
            override fun onClickItem(data: ResponseGetAllUserPartnerItem) {
                val bundle = Bundle()
                bundle.putString(PARTNER_ID, data.idMitra)
                findNavController().navigate(
                    R.id.action_superAdminHomeFragment_to_superAdminApprovalFragment,
                    bundle
                )
//                if (data.status == "deactive") {
//                    findNavController().navigate(
//                        R.id.action_superAdminHomeFragment_to_superAdminApprovalFragment,
//                        bundle
//                    )
//                } else if (data.status == "active") {
//                    findNavController().navigate(
//                        R.id.action_superAdminHomeFragment_to_superAdminDisableFragment,
//                        bundle
//                    )
//                }
            }
        })
        binding.rvUser.adapter = userAdapter
    }

    private fun getUserPartner() {
        binding.rvUser.visibility = View.GONE
        progressDialog.show()
        viewModel.getUser()
        viewModel.getAllUserPartner.removeObservers(viewLifecycleOwner)
        Handler(Looper.getMainLooper()).postDelayed({
        viewModel.getAllUserPartner.observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    when (it.data?.code()) {
                        200 -> {
                            if (!it.data.body().isNullOrEmpty()) {
                                listUser.clear()
                                listApproved.clear()
                                listWaiting.clear()
                                listUser.addAll(it.data.body()!!)

                                Handler(Looper.getMainLooper()).postDelayed({
                                    for (i in listUser) {
                                        if (i.status == "deactive" && i.role == "2") {
                                            listWaiting.add(i)
                                        } else if (i.status == "active" && i.role == "2") {
                                            listApproved.add(i)
                                        }
                                    }
                                    progressDialog.dismiss()
                                    binding.rvUser.visibility = View.VISIBLE
                                    userAdapter.submitData(listWaiting)
                                }, 1000)
                                viewModel.getAllUserPartner.removeObservers(viewLifecycleOwner)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Belum ada mitra yang mendaftar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                "Belum ada mitra yang mendaftar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                ERROR -> {
                    viewModel.getAllUserPartner.removeObservers(viewLifecycleOwner)
                    binding.rvUser.visibility = View.GONE
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
                    binding.rvUser.visibility = View.GONE
                    progressDialog.show()
                }
            }

        }
        },1000)

    }

    private fun setupButton() {
        binding.apply {
            btnApproved.setOnClickListener {
                btnWaiting.strokeColor = ColorStateList.valueOf(Color.rgb(0, 173, 181))
                btnWaiting.strokeWidth = 3
                btnWaiting.setTextColor(Color.parseColor("#00ADB5"))
                btnWaiting.setBackgroundColor(Color.parseColor("#EEEEEE"))

                btnApproved.strokeWidth = 0
                btnApproved.setTextColor(Color.parseColor("#EEEEEE"))
                btnApproved.setBackgroundColor(Color.parseColor("#00ADB5"))

                userAdapter.submitData(listApproved)

            }
            btnWaiting.setOnClickListener {
                btnApproved.strokeColor = ColorStateList.valueOf(Color.rgb(0, 173, 181))
                btnApproved.strokeWidth = 3
                btnApproved.setTextColor(Color.parseColor("#00ADB5"))
                btnApproved.setBackgroundColor(Color.parseColor("#EEEEEE"))

                btnWaiting.strokeWidth = 0
                btnWaiting.setTextColor(Color.parseColor("#EEEEEE"))
                btnWaiting.setBackgroundColor(Color.parseColor("#00ADB5"))

                userAdapter.submitData(listWaiting)

            }
        }
    }
}

