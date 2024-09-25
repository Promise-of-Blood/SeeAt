package com.pob.seeat.presentation.view.feed

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.util.FusedLocationSource
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentSelectLocateBinding
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SelectLocateFragment : Fragment() {
    private var _binding: FragmentSelectLocateBinding? = null
    private val binding get() = _binding!!

    private var initLatitude: Float? = null
    private var initLongitude: Float? = null
    private var initZoom: Float? = null

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var isMoving = false

    private var isLocationTrackingEnabled = false

    private val viewModel: NewFeedViewModel by activityViewModels()

    private lateinit var currentLatLng: LatLng

    private var locationSelectedListener: OnLocationSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationSelectedListener = parentFragment as? OnLocationSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            initLatitude = it.getFloat("homeLatitude")
            initLongitude = it.getFloat("homeLongitude")
            initZoom = it.getFloat("homeZoom")
        } ?: run{
            (requireActivity() as MainActivity).getCurrentLocation { location ->
                initLatitude = location!!.latitude.toFloat()
                initLongitude = location.longitude.toFloat()
                initZoom = 16f
            }
        }

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
                clSelectLocation.visibility = View.GONE
            }

            tvConfirmLocate.setOnClickListener {
                locationSelectedListener?.onLocationSelected(currentLatLng)
                clSelectLocation.visibility = View.GONE
            }
//            tvConfirmLocate.setOnClickListener {
//                Timber.i("changed Location $currentLatLng")
//                viewModel.updateSelectLocation(currentLatLng)
//                requireActivity().onBackPressed()
//            }

            ibLocation.setOnClickListener {
                changeStatusLocationButton()
            }
        }

        // 뒤로가기 누를 시 현재 프래그먼티 visiblity = View.GONE
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.clSelectLocation.visibility == View.VISIBLE) {
                        // 뒤로 가기 누르면 View.GONE 처리
                        binding.clSelectLocation.visibility = View.GONE
                    } else {
                        // 기본 뒤로가기 동작 수행
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }


    private fun changeStatusLocationButton() {
        isLocationTrackingEnabled = !isLocationTrackingEnabled
        Timber.tag("HomeFragment")
            .d("isLocationTrackingEnabled: " + isLocationTrackingEnabled)

        binding.apply {
            if (isLocationTrackingEnabled) {
                // 위치 추적 모드로 전환
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                ibLocation.imageTintList = ColorStateList.valueOf(
                    getColor(
                        requireContext(),
                        R.color.primary
                    )
                )
            } else {
                // 위치 추적 모드 해제
                naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
                ibLocation.imageTintList = ColorStateList.valueOf(
                    getColor(
                        requireContext(),
                        R.color.light_gray
                    )
                )
            }
        }
    }


    private fun initNaverMap() {
        Timber.tag("SelectLocateFragment").d("initNaverMap")

        val options = NaverMapOptions()

        // 현재 위치를 LatLng 객체로 생성
        val homeLatLng = LatLng(initLatitude!!.toDouble(), initLongitude!!.toDouble())

        // NaverMapOptions에 CameraPosition 설정
        options
            .camera(CameraPosition(homeLatLng, initZoom!!.toDouble())) // 카메라 위치에 현위치 좌표를 설정

        val mapFragment = MapFragment.newInstance(options).also {
            childFragmentManager.beginTransaction().add(R.id.map, it).commit()
        }

        locationSource = FusedLocationSource(this, 1000)

        mapFragment.getMapAsync { naverMap ->
            this.naverMap = naverMap
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            naverMap.isIndoorEnabled = true
            naverMap.locationSource = locationSource

            val uiSettings = naverMap.uiSettings
            uiSettings.apply {
                isLocationButtonEnabled = false
                isCompassEnabled = false
                isZoomControlEnabled = false
                isTiltGesturesEnabled = false
                isScaleBarEnabled = false
            }

            naverMap.let {
                naverMap.addOnCameraChangeListener { reason, animated ->
                    if (!isMoving) {
                        isMoving = true
                        Timber.tag("SelectLocateFragment")
                            .d("카메라가 움직이고 있습니다. Reason: " + reason + ", Animated: " + animated)

                        if (reason == -1) {
                            isLocationTrackingEnabled = true
                            floatingMarker(binding.ivMarker, true)
                            changeStatusLocationButton()
                        }
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

    override fun onDetach() {
        super.onDetach()
        locationSelectedListener = null
    }
}

