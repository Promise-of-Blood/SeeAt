package com.pob.seeat.presentation.view.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.pob.seeat.R
import com.pob.seeat.presentation.service.NaverMapWrapper
import com.pob.seeat.databinding.FragmentSelectLocateBinding
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SelectLocateFragment : Fragment() {
    private var _binding: FragmentSelectLocateBinding? = null
    private val binding get() = _binding!!

    private var isMoving = false

    private val viewModel: NewFeedViewModel by activityViewModels()

    @Inject
    lateinit var naverMapWrapper: NaverMapWrapper  // NaverMapWrapper를 Hilt로 주입받음

    private lateinit var currentLatLng: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectLocateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialSetting()
        initNaverMap()
    }

    private fun initialSetting() {
        binding.apply {
            toolbarMessage.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            tvConfirmLocate.setOnClickListener {
                viewModel.updateSelectLocation(currentLatLng)
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initNaverMap() {
        Timber.tag("SelectLocateFragment").d("initNaverMap")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        naverMapWrapper.initialize(mapFragment)

        // StateFlow로 naverMap 객체를 구독하여 값이 설정되면 작업 처리
        lifecycleScope.launchWhenResumed {
            naverMapWrapper.getNaverMap().collect { naverMap ->
                naverMap?.let {
                    Timber.tag("SelectLocateFragment").d("NaverMap 객체가 초기화되었습니다.")
                    naverMap.addOnCameraChangeListener { reason, animated ->
                        if (!isMoving) {
                            isMoving = true
                            Timber.tag("SelectLocateFragment")
                                .d("카메라가 움직이고 있습니다. Reason: " + reason + ", Animated: " + animated)
                            floatingMarker(binding.ivMarker, true)
                        }
                    }

                    naverMap.addOnCameraIdleListener {
                        if (isMoving) {
                            isMoving = false
                            Timber.tag("SelectLocateFragment").d("카메라 움직임이 멈췄습니다.")
                            floatingMarker(binding.ivMarker, false)
                            currentLatLng = naverMap.cameraPosition.target
                            Timber.tag("SelectLocateFragment").d("맵 중앙 위치 %s", currentLatLng)
                        }
                    }
                } ?: Timber.tag("SelectLocateFragment")
                    .d("NaverMap 객체가 아직 null입니다.")  // NaverMap이 null일 때 로그 추가
            }
        }
    }

    private fun floatingMarker(markerView: View, isFloating: Boolean) {
        if (isFloating) {
            // 마커를 위로 이동시키는 애니메이션 (공중에 뜨게 함)
            markerView.animate()
                .translationY(-40f)
                .setDuration(250)
                .start()
        } else {
            // 마커를 원래 위치로 되돌리는 애니메이션
            markerView.animate()
                .translationY(0f)
                .setDuration(250)
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
