package com.anantyan.firestoremvvm.ui.main.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anantyan.firestoremvvm.databinding.FragmentHomeBinding
import com.anantyan.firestoremvvm.ui.main.MainViewModel
import com.anantyan.firestoremvvm.utils.Resource
import com.anantyan.firestoremvvm.utils.onDecorationListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

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
        setupRecyclerView()
        setupInteractions()
        onBindObserver()
    }

    private fun setupRecyclerView() {
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
            showDeleteConfirmationDialog(id)
        }

        binding.rvList.setHasFixedSize(true)
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.itemAnimator = DefaultItemAnimator()
        binding.rvList.isNestedScrollingEnabled = false
        binding.rvList.addItemDecoration(requireContext().onDecorationListener(RecyclerView.VERTICAL, 16))
        binding.rvList.adapter = concatAdapter

        binding.swipeRefresh.setOnRefreshListener {
            homeAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.btnRetry.setOnClickListener { homeAdapter.retry() }
    }

    private fun setupInteractions() {
        binding.btnAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddFragment(title = "Add")
            findNavController().navigate(action)
        }

        binding.btnRefreshLocation.setOnClickListener {
            viewModel.currentLocation()
        }

        binding.btnMain.setOnClickListener {
            if (binding.btnRefreshLocation.visibility == View.INVISIBLE) {
                binding.btnRefreshLocation.visibility = View.VISIBLE
                binding.btnAdd.visibility = View.VISIBLE

                binding.btnRefreshLocation.animate().translationY(-binding.btnMain.height.toFloat()-16f).start()
                binding.btnAdd.animate().translationX(-binding.btnMain.width.toFloat()-16f).start()
            } else {
                binding.btnRefreshLocation.animate().translationY(0f).withEndAction {
                    binding.btnRefreshLocation.visibility = View.INVISIBLE
                }.start()

                binding.btnAdd.animate().translationX(0f).withEndAction {
                    binding.btnAdd.visibility = View.INVISIBLE
                }.start()
            }
        }
    }

    private fun showDeleteConfirmationDialog(id: String) {
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

    private fun onBindObserver() {
        viewModel.getAll().observe(viewLifecycleOwner) {
            homeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.currentLocation.observe(viewLifecycleOwner) {
            handleCurrentLocation(it)
        }
        viewModel.currentLocation()
    }

    private fun handleCurrentLocation(resource: Resource<Boolean>) {
        when (resource) {
            is Resource.Loading -> showLoadingState()
            is Resource.Success -> showSuccessState(resource.data)
            else -> {}
        }
    }

    private fun showLoadingState() {
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showSuccessState(data: Boolean) {
        binding.txtNotLocation.isVisible = data
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}