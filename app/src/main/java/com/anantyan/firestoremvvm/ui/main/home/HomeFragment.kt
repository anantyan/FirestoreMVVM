package com.anantyan.firestoremvvm.ui.main.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.anantyan.firestoremvvm.databinding.FragmentHomeBinding
import com.anantyan.firestoremvvm.ui.main.MainViewModel
import com.anantyan.firestoremvvm.utils.Resource
import com.anantyan.firestoremvvm.utils.onDecorationListener
import com.anantyan.firestoremvvm.utils.onFABListener
import com.anantyan.firestoremvvm.utils.onSnackSuccess
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var homeAdapter: HomeAdapter
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
        onBindView()
        onBindObserver()
    }

    private fun onBindObserver() {
        viewModel.getAll().observe(viewLifecycleOwner) {
            homeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.write.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                requireContext().onSnackSuccess(binding.root, "Berhasil dihapus, ampun suhu 🙏")
            }
        }
    }

    private fun onBindView() {
        homeAdapter = HomeAdapter()
        val loadStateHeader = HomeLoadStateAdapter { homeAdapter.retry() }
        val loadStateFooter = HomeLoadStateAdapter { homeAdapter.retry() }
        val concatAdapter = homeAdapter.withLoadStateHeaderAndFooter(
            header = loadStateHeader,
            footer = loadStateFooter
        )

        homeAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        homeAdapter.addLoadStateListener {
            binding.swipeRefresh.isRefreshing = it.source.refresh is LoadState.Loading
            binding.rvList.isVisible = it.source.refresh is LoadState.NotLoading
            binding.layoutContent.isVisible = it.source.refresh is LoadState.Error
            binding.txtNotFound.isVisible = it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && homeAdapter.itemCount <= 0
        }
        homeAdapter.onClick { _, id ->
            val action = HomeFragmentDirections.actionHomeFragmentToAddFragment(id, true, "Update")
            findNavController().navigate(action)
        }
        homeAdapter.onLongClick { _, id ->
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Warning!")
            builder.setMessage("Apakah anda ingin hapus data ${id}?")
            builder.setPositiveButton("Iya", null)
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.show()
            val btnPositif = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnPositif.setOnClickListener {
                viewModel.delete(id)
                dialog.dismiss()
                homeAdapter.refresh()
            }
        }

        binding.rvList.setHasFixedSize(true)
        binding.rvList.itemAnimator = DefaultItemAnimator()
        binding.rvList.isNestedScrollingEnabled = false
        binding.rvList.addItemDecoration(requireContext().onDecorationListener(RecyclerView.VERTICAL, 16))
        binding.rvList.addOnScrollListener(binding.btnAdd.onFABListener())
        binding.rvList.adapter = concatAdapter

        binding.swipeRefresh.setOnRefreshListener { homeAdapter.refresh() }
        binding.btnRetry.setOnClickListener { homeAdapter.retry() }
        binding.btnAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddFragment(title = "Add")
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}