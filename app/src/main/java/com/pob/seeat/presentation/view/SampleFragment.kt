package com.pob.seeat.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentSampleBinding
import com.pob.seeat.presentation.viewmodel.SampleViewModel
import com.pob.seeat.presentation.viewmodel.SampleViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SampleFragment : Fragment() {
    companion object {
        fun newInstance() = SampleFragment
    }


    private var _binding: FragmentSampleBinding? = null
    private val binding get() = _binding!!

    private val sampleViewmodel by viewModels<SampleViewModel> {
        SampleViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSampleBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sampleViewmodel.getSampleImageList(query = "검색어")

        with(sampleViewmodel){
            viewLifecycleOwner.lifecycleScope.launch {
                sampleUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collectLatest {
                        when(it){
                            is UiState.Error ->{
//                                binding.progressBar.isVisible = false
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }

                            is UiState.Loading -> {
//                                binding.progressBar.isVisible = true
//                                binding.searchRecyclerview.isVisible = false
                            }

                            is UiState.Success -> {
//                                binding.progressBar.isVisible = false
//                                binding.searchRecyclerview.isVisible = true
//                                searchListAdapter.submitList(it.data)
                            }
                        }
                    }
            }
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}