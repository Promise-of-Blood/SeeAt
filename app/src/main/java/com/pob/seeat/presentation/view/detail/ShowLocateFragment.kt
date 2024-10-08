package com.pob.seeat.presentation.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentShowLocateBinding
import com.pob.seeat.presentation.view.admin.AdminActivity
import com.pob.seeat.utils.Utils.px
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShowLocateFragment : Fragment() {
    private var _binding: FragmentShowLocateBinding? = null
    private val binding get() = _binding!!

    val args: ShowLocateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowLocateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialSetting()
        initNaverMap()
        if (activity is AdminActivity) initAdminView()
    }

    private fun initAdminView() = with(binding) {
        (activity as AdminActivity).setNavigateButton()
        root.fitsSystemWindows = false
        toolbarMessage.visibility = View.GONE
    }

    private fun initialSetting() {
        binding.apply {
            toolbarMessage.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun initNaverMap() {
        Timber.tag("SelectLocateFragment").d("initNaverMap")
        val options = NaverMapOptions()
            .camera(
                CameraPosition(
                    LatLng(
                        args.feedLatitude.toDouble(),
                        args.feedLongitude.toDouble()
                    ), 16.0
                )
            )
            .mapType(NaverMap.MapType.Basic)

        val mapFragment = MapFragment.newInstance(options)

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.map, mapFragment) // R.id.map은 FrameLayout의 ID
        transaction.commit()

        val marker = Marker()


        mapFragment.getMapAsync { naverMap ->
            marker.position = LatLng(args.feedLatitude.toDouble(), args.feedLongitude.toDouble())
            marker.map = naverMap
            marker.icon = OverlayImage.fromResource(R.drawable.ic_location_pin)
            marker.iconTintColor = resources.getColor(R.color.primary)
            marker.width = 42f.px
            marker.height = 42f.px
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