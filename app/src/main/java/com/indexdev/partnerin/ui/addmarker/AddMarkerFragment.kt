package com.indexdev.partnerin.ui.addmarker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.indexdev.partnerin.databinding.FragmentAddMarkerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMarkerFragment : Fragment() {
    private var _binding: FragmentAddMarkerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMarkerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}