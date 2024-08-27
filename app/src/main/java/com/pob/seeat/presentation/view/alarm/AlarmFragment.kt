package com.pob.seeat.presentation.view.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentAlarmBinding
import com.pob.seeat.presentation.model.Alarm

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlarmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlarmFragment : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val alarmAdapter by lazy { AlarmAdapter(::handleClickAlarm) }
    private var dummyAlarm = listOf(
        Alarm("1", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "like", true),
        Alarm("2", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "like", true),
        Alarm("3", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "comment", true),
        Alarm("4", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "comment", true),
        Alarm("5", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "like", true),
        Alarm("6", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "like"),
        Alarm("7", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "comment"),
        Alarm("8", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "comment"),
        Alarm("9", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "comment", true),
        Alarm("10", "으아 어이없어", "나 공명선인데 나 140 맞다", "2분전", "like"),
    )

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
        alarmAdapter.submitList(dummyAlarm)
    }

    private fun handleClickAlarm(alarm: Alarm) {
        // TODO: 클릭한 알림에 해당하는 게시물 상세 페이지로 이동
        dummyAlarm = dummyAlarm.map { if (it.id == alarm.id) it.copy(isRead = true) else it }
        alarmAdapter.submitList(dummyAlarm)
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