package com.example.main.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.common.utils.Resource
import com.example.core.data.model.Note
import com.example.main.databinding.FragmentAddBinding
import com.example.main.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()
    private val args by navArgs<AddFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindObserver()
        onBindView()
    }

    private fun onBindObserver() {
        viewModel.read.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                binding.txtTitle.setText(it.data.title)
                binding.txtContent.setText(it.data.content)
                binding.boolPublish.isChecked = it.data.publish == true
            }
        }
    }

    private fun onBindView() {
        if (args.navUpdate) {
            viewModel.getById(args.idNote)
            binding.btnSave.setOnClickListener {
                val note = Note(
                    title = binding.txtTitle.text.toString(),
                    content = binding.txtContent.text.toString(),
                    publish = binding.boolPublish.isChecked
                )
                viewModel.update(args.idNote, note)
            }
        } else {
            binding.btnSave.setOnClickListener {
                val note = Note(
                    title = binding.txtTitle.text.toString(),
                    content = binding.txtContent.text.toString(),
                    publish = binding.boolPublish.isChecked
                )
                viewModel.insert(note)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}