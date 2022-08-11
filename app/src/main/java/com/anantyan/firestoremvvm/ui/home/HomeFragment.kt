package com.anantyan.firestoremvvm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.anantyan.firestoremvvm.databinding.FragmentHomeBinding
import com.anantyan.firestoremvvm.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindObserver()
        onBindView()
    }

    private fun onBindView() {
        TODO("Not yet implemented")
    }

    private fun onBindObserver() {
        viewModel.getAll.observe(viewLifecycleOwner) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}