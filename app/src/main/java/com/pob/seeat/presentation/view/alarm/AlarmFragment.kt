package com.pob.seeat.presentation.view.alarm

import android.graphics.Canvas
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentAlarmBinding
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.presentation.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmFragment : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val alarmAdapter by lazy { AlarmAdapter(::handleClickAlarm) }
    private val alarmViewModel by viewModels<AlarmViewModel>()

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
        handleSwipeAlarm()
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
                    when (response) {
                        is Result.Error -> {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                                .show()
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
        val action = AlarmFragmentDirections.actionAlarmToDetail(alarm.feedId)
        findNavController().navigate(action)
        alarmViewModel.readAlarm(alarm.alarmId)
    }

    private fun handleSwipeAlarm() {
        val itemTouchCallback = getItemTouchCallback()
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.rvAlarm)
    }

    private fun getItemTouchCallback() = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP, ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val alarmId = alarmAdapter.getAlarmId(viewHolder.layoutPosition)
            alarmViewModel.deleteAlarm(alarmId)
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return 2f // 반 이상 스와이프 시 자동으로 아이템뷰 삭제
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            val view = (viewHolder as AlarmAdapter.Holder).getView()
            getDefaultUIUtil().clearView(view)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            val view = (viewHolder as? AlarmAdapter.Holder)?.getView()
            viewHolder?.let {
                getDefaultUIUtil().onSelected(view)
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val view = (viewHolder as AlarmAdapter.Holder).getView()
                getDefaultUIUtil().onDraw(
                    c,
                    recyclerView,
                    view,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }
}