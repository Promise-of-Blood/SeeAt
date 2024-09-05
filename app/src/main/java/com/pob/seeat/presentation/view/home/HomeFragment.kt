package com.pob.seeat.presentation.view.home

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.ClusteringKey
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MapConstants
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.viewmodel.HomeViewModel
import com.pob.seeat.presentation.viewmodel.RestroomViewModel
import com.pob.seeat.utils.Utils.px
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.repository.NaverMapWrapper
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.utils.Utils.tagList
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val TAG = "PersistentActivity"
    private val restroomViewModel: RestroomViewModel by viewModels()

    @Inject
    lateinit var naverMapWrapper: NaverMapWrapper

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var isLocationTrackingEnabled = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var isExpanded = false

    private val homeViewModel: HomeViewModel by viewModels()

    private val bottomSheetFeedAdapter: BottomSheetFeedAdapter by lazy { BottomSheetFeedAdapter(::handleClickFeed) }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRestroomViewModel()
        initNaverMap()
        initTagRecyclerView()
        initBottomSheet()
        getFeed()
        initialSetting()
    }

    private fun initialSetting() {
        binding.run {
            ibAddMarker.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_new_feed)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getFeed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getFeed() = with(homeViewModel) {

        getFeedList()

        viewLifecycleOwner.lifecycleScope.launch {
            feedResponse.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { response ->
                    when (response) {
                        is Result.Error -> {
                            Timber.tag("HomeFragment").e("Error: %s", response.message)
                        }

                        is Result.Loading -> {
                            Timber.tag("HomeFragment").d("Loading..")
                        }

                        is Result.Success -> {
                            val feedList = response.data
                            Timber.tag("HomeFragment").d("Result.Success: " + feedList.toString())
                            bottomSheetFeedAdapter.submitList(feedList)
                            updateMarker(feedList)
                            Timber.tag("HomeFragment").d(feedList.toString())
                        }
                    }
                }
        }
    }

    private class ItemKey(val id: String, private val position: LatLng) : ClusteringKey {
        override fun getPosition() = position

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val itemKey = other as ItemKey
            return id == itemKey.id
        }

        override fun hashCode() = id.hashCode()
    }

    /**
     * 모든 feed 도큐먼트를 가져와 해당 좌표값에 마커 생성
     * 클릭 시 로그로 정보확인 가능
     */
    private fun updateMarker(feedList: List<FeedModel>) {
        Timber.tag("HomeFragment").d("Enter UpdateMarker..")
        if (!::naverMap.isInitialized) {
            Timber.tag("HomeFragment").e("naverMap is not initialized")
            return
        }

        var clusterer: Clusterer<ItemKey>? = null
        val listSize = feedList.size

        clusterer = Clusterer.Builder<ItemKey>()
            .leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
                override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                    super.updateLeafMarker(info, marker)
                    marker.icon = Marker.DEFAULT_ICON

                    marker.onClickListener = Overlay.OnClickListener {
                        // ItemKey의 id를 통해 feedList에서 FeedModel을 가져옴
                        val feedModel = feedList.find { it.feedId == (info.key as ItemKey).id }
                        feedModel?.let { model ->
                            Timber.tag("HomeFragment")
                                .d("%s%s", "Marker Clicked: " + model.feedId + ", " + model.title + ", ", model.content)
                        } ?: Timber.tag("HomeFragment").e("FeedModel not found for marker")
                        true
                    }
                }
            })
            .build()
            .apply {
                val keyTagMap = buildMap(listSize) {
                    repeat(listSize) { i ->
                        Timber.tag("HomeFragment").d("FeedModel: ${feedList[i]}")
                        val latitude = feedList[i].location?.latitude
                        val longitude = feedList[i].location?.longitude
                        Timber.tag("HomeFragment").d("Latitude: $latitude, Longitude: $longitude")
                        if (latitude != null && longitude != null) {
                            put(
                                ItemKey(
                                    feedList[i].feedId,  // FeedModel의 feedId를 사용하여 ItemKey 생성
                                    LatLng(latitude, longitude),
                                ),
                                (Math.random() * 5).toInt(),
                            )
                        }
                    }
                }

                addAll(keyTagMap)
                map = naverMap
            }
    }


    /**
     * 네이버 지도 설정하는 코드
     *
     * TODO 네이버 로고, ScaleBar 의 위치를
     *  BottomSheet 의 halfExpanded 까지 따라올 수 있게 구현
     *
     *  TODO GPS 버튼 클릭, 위치 권한 미설정 시 재요청
     * */
    private fun initNaverMap() {
        // 위치 소스 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        naverMapWrapper.initialize(mapFragment)

        // StateFlow로 naverMap 객체를 구독하여 값이 설정되면 작업 처리
        lifecycleScope.launchWhenStarted {
            naverMapWrapper.getNaverMap().collect { naverMap ->
                naverMap?.let {
                    setupNaverMap(it)
                }
            }
        }

        binding.apply {
            ibLocation.setOnClickListener {
                if (naverMap != null) {
                    isLocationTrackingEnabled = !isLocationTrackingEnabled
                    Timber.tag("HomeFragment")
                        .d("isLocationTrackingEnabled: " + isLocationTrackingEnabled)
                    if (isLocationTrackingEnabled) {
                        // 위치 추적 모드로 전환
                        naverMap.locationTrackingMode = LocationTrackingMode.Follow
                        ibLocation.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.primary
                            )
                        )
                    } else {
                        // 위치 추적 모드 해제
                        naverMap.locationTrackingMode = LocationTrackingMode.None
                        ibLocation.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_gray
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setupNaverMap(naverMap: NaverMap) {
        this.naverMap = naverMap
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

        val scaleBarView = binding.naverScaleBar
        scaleBarView.map = naverMap

        naverMap.addOnCameraChangeListener { reason, animated ->
            Timber.tag("HomeFragment")
                .d("카메라가 움직이고 있습니다. Reason: " + reason + ", Animated: " + animated)
        }

        // 카메라 움직임이 멈췄을 때 콜백을 받는 리스너 설정
        naverMap.addOnCameraIdleListener {
            Timber.tag("HomeFragment").d("카메라 움직임이 멈췄습니다.")
        }

    }

    /**
     * 태그 리스트 Recycler View 설정
     * */
    private fun initTagRecyclerView() {
        // 태그 리스트 데이터 설정

        binding.apply {
            val adapter = TagAdapter(tagList)
            rvTagList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvTagList.adapter = adapter

            val marginDecoration = MarginItemDecoration(16f.px) // 마진 설정
            rvTagList.addItemDecoration(marginDecoration)
        }
    }

    /**
     * 바텀시트 기본 설정 및
     * 상태별 동작 설정
     */
    private fun initBottomSheet() {
        //BottomSheet 옵션 설정
        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)

        binding.rvBottomSheetPostList.adapter = bottomSheetFeedAdapter
        binding.rvBottomSheetPostList.layoutManager = LinearLayoutManager(requireContext())

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet가 최대 높이에 도달했을 때 수행할 동작
                        isExpanded = true
                        onBottomSheetExpanded()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED,
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        isExpanded = false
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        // BottomSheet의 드래그 상태에서 수행할 동작
                        if (isExpanded) {
                            onBottomSheetDragging()
                        }
                    }

                    else -> {
                        // 다른 상태의 경우 플래그를 false로 설정
                        isExpanded = false
                    }
                }
            }

            private fun onBottomSheetExpanded() {
                binding.apply {
                    // 배경색 애니메이션 (투명 -> 흰색)
                    animateBackgroundColor(clTopBar, R.color.transparent, R.color.white)

                    // bottomSheet 배경 변경
                    persistentBottomSheet.setBackgroundResource(R.color.white)

                    // viewTopBar의 투명도를 서서히 줄여서 보이지 않게 함
                    viewTopBar.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .start()
                }
            }

            private fun onBottomSheetDragging() {
                binding.apply {
                    // 배경색 애니메이션 (흰색 -> 투명)
                    animateBackgroundColor(clTopBar, R.color.white, R.color.transparent)

                    // bottomSheet 배경 변경
                    persistentBottomSheet.setBackgroundResource(R.drawable.white_round_top_border_20)

                    viewTopBar.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
            }

            private fun animateBackgroundColor(
                view: View,
                startColorResId: Int,
                endColorResId: Int
            ) {
                val startColor = ContextCompat.getColor(requireContext(), startColorResId)
                val endColor = ContextCompat.getColor(requireContext(), endColorResId)

                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
                colorAnimation.duration = 300 // 애니메이션 시간 (밀리초)
                colorAnimation.addUpdateListener { animator ->
                    view.setBackgroundColor(animator.animatedValue as Int)
                }
                colorAnimation.start()
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // BottomSheet의 슬라이드 상태에 따라 호출됨 (0.0f ~ 1.0f)
            }
        })

        // 바텀 시트의 최대 높이를 태그 리스트 하단까지 이동
        binding.rvTagList.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 태그 리스트의 하단 위치 구하기
                // rvLocation[0]: 태그 리스트의 x 좌표
                // rvLocation[1]: 태그 리스트의 y 좌표
                val rvLocation = IntArray(2)
                binding.rvTagList.getLocationOnScreen(rvLocation)

                val rvBottomY = rvLocation[1] + binding.rvTagList.height - 31.0f.px

                bottomSheetBehavior.expandedOffset = rvBottomY

                // Listener 제거
                binding.rvTagList.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 부여되었을 때 위치 추적 모드를 설정
                if (::naverMap.isInitialized) {
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                }
            } else {
                // 권한이 거부된 경우
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleClickFeed(feedModel: FeedModel) {
        val action = HomeFragmentDirections.actionNavigationHomeToNavigationDetail(feedModel.feedId)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    private fun initRestroomViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            restroomViewModel.getRestroomUiState()
            restroomViewModel.restroomUiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            Log.e("Restroom", "화장실 데이터 없음 ${uiState.message}")
                        }

                        UiState.Loading -> {
                            Log.d("Restroom", "화장실 데이터 로딩")
                        }

                        is UiState.Success -> {
                            Log.d("Restroom", "화장실 데이터 : ${uiState.data}")
                        }
                    }
                }
        }
    }
}

class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                left = spaceHeight // 첫 번째 아이템에는 왼쪽 마진을 추가
            }
            right = spaceHeight // 오른쪽 마진을 모든 아이템에 추가
        }
    }
}