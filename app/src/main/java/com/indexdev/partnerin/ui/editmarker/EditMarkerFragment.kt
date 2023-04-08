package com.indexdev.partnerin.ui.editmarker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.indexdev.partnerin.R
import com.indexdev.partnerin.databinding.FragmentEditMarkerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMarkerFragment : Fragment() {
    private var _binding: FragmentEditMarkerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMarkerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}