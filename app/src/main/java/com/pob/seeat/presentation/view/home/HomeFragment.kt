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
import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.messaging.FirebaseMessaging
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.viewmodel.RestroomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val TAG = "PersistentActivity"
    private val restroomViewModel: RestroomViewModel by viewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var isLocationTrackingEnabled = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var isExpanded = false

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
    }

    /**
    * 네이버 지도 설정하는 코드
    * TODO 네이버 로고, ScaleBar 의 위치를
    *  BottomSheet 의 halfExpanded 까지 따라올 수 있게 구현
    * */
    private fun initNaverMap() {
        // 위치 소스 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val mapFragment = MapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.map, mapFragment)
            .commit()

        // NaverMap 객체를 얻기 위해 비동기로 콜백 설정
        mapFragment.getMapAsync { naverMap ->
            this.naverMap = naverMap
            naverMap.isIndoorEnabled = true
            naverMap.locationSource = locationSource

            val uiSettings = naverMap.uiSettings
            uiSettings.apply {
                isLocationButtonEnabled = false
                isCompassEnabled = false
                isZoomControlEnabled = true
                isTiltGesturesEnabled = false
                isScaleBarEnabled = false
            }

            val scaleBarView = binding.naverScaleBar
            scaleBarView.map = naverMap
        }
    }

    /**
    * 태그 리스트 Recycler View 설정
    * */
    private fun initTagRecyclerView() {
        // 태그 리스트 데이터 설정
        val tagList = listOf(
            Tag("전체", R.drawable.ic_map, Color.parseColor("#2ECC87")),
            Tag("맛집 추천", R.drawable.ic_soup, Color.parseColor("#FFCF30")),
            Tag("모임", R.drawable.ic_group, Color.parseColor("#A2FF77")),
            Tag("술 친구", R.drawable.ic_beer_strok, Color.parseColor("#2ECC87")),
            Tag("운동 친구", R.drawable.ic_gym, Color.parseColor("#2ECC87")),
            Tag("스터디", R.drawable.ic_pencil, Color.parseColor("#FF9500")),
            Tag("분실물", R.drawable.ic_lost_item, Color.parseColor("#FFAA75")),
            Tag("정보공유", R.drawable.ic_info, Color.parseColor("#5145FF")),
            Tag("질문", R.drawable.ic_question, Color.parseColor("#717171")),
            Tag("산책", R.drawable.ic_paw, Color.parseColor("#FF9CE1")),
            Tag("밥친구", R.drawable.ic_restaurant, Color.parseColor("#FFC300")),
            Tag("노래방", R.drawable.ic_microphone_line, Color.parseColor("#9A7EFF")),
            Tag("도움", R.drawable.ic_flag, Color.parseColor("#5196FF")),
            Tag("긴급", R.drawable.ic_megaphone, Color.parseColor("#FF3939")),
            Tag("기타", R.drawable.ic_sparkles, Color.parseColor("#FFDF60"))
        )

        binding.apply {
            val adapter = TagAdapter(tagList)
            rvTagList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvTagList.adapter = adapter

            val marginDecoration = MarginItemDecoration(40) // 마진 설정
            rvTagList.addItemDecoration(marginDecoration)

            ibLocation.setOnClickListener {
                if (::naverMap.isInitialized) {
                    isLocationTrackingEnabled = !isLocationTrackingEnabled
                    Log.d("HomeFragment", "isLocationTrackingEnabled: $isLocationTrackingEnabled")
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

    private fun initBottomSheet() {
        //BottomSheet 옵션 설정
        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)

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
        binding.rvTagList.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 태그 리스트의 하단 위치 구하기
                // rvLocation[0]: 태그 리스트의 x 좌표
                // rvLocation[1]: 태그 리스트의 y 좌표
                val rvLocation = IntArray(2)
                binding.rvTagList.getLocationOnScreen(rvLocation)

                val rvBottomY = rvLocation[1] + binding.rvTagList.height

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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