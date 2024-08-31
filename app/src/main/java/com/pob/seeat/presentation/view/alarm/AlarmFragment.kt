package com.pob.seeat.presentation.view.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentAlarmBinding
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.presentation.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlarmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AlarmFragment : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val alarmAdapter by lazy { AlarmAdapter(::handleClickAlarm) }
    private val alarmViewModel by viewModels<AlarmViewModel>()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        alarmViewModel.getAlarmList()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
        _binding = null
    }

    private fun initView() = with(binding) {
        (activity as MainActivity).setBottomNavigationVisibility(View.GONE)
        tbAlarm.apply {
            setNavigationIcon(R.drawable.ic_arrow_left)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        rvAlarm.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initViewModel() = with(alarmViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            alarmResponse.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { response ->
                    when(response){
                        is Result.Error ->{
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Loading -> {
                            binding.rvAlarm.isVisible = false
                        }

                        is Result.Success -> {
                            binding.rvAlarm.isVisible = true
                            alarmAdapter.submitList(response.data)
                        }
                    }
                }
        }
    }

    private fun handleClickAlarm(alarm: AlarmModel) {
        // TODO: 클릭한 알림에 해당하는 게시물 상세 페이지로 이동
        alarmViewModel.readAlarm(alarm.uId)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlarmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlarmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}