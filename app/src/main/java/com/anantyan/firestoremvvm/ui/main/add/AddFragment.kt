package com.anantyan.firestoremvvm.ui.main.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.anantyan.firestoremvvm.databinding.FragmentAddBinding
import com.anantyan.firestoremvvm.model.Note
import com.anantyan.firestoremvvm.ui.main.MainViewModel
import com.anantyan.firestoremvvm.utils.Resource
import com.anantyan.firestoremvvm.utils.onSnackError
import com.anantyan.firestoremvvm.utils.onSnackSuccess
import com.anantyan.firestoremvvm.utils.onToast
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
                binding.txtTitle.setText(it.data?.title.toString())
                binding.txtContent.setText(it.data?.content.toString())
                binding.boolPublish.isChecked = it.data?.publish == true
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