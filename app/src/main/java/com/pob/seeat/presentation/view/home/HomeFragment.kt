package com.pob.seeat.presentation.view.home

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapFragment
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val TAG = "PersistentActivity"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val mapFragment = MapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit()

            // NaverMap 객체를 얻기 위해 비동기로 콜백 설정
            mapFragment.getMapAsync { naverMap ->
                naverMap.isIndoorEnabled = true
            }
        }
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

            val marginDecoration = MarginItemDecoration(24) // 16dp 마진
            rvTagList.addItemDecoration(marginDecoration)
        }

        persistentBottomSheetEvent()
    }

    private fun persistentBottomSheetEvent() {
        behavior = BottomSheetBehavior.from(binding.persistentBottomSheet)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 되는 도중 계속 호출
                Log.d(TAG, "onStateChanged: 드래그 중")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d(TAG, "onStateChanged: 접음")
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d(TAG, "onStateChanged: 드래그")
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d(TAG, "onStateChanged: 펼침")
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d(TAG, "onStateChanged: 숨기기")
                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                        Log.d(TAG, "onStateChanged: 고정됨")
                    }
                }
            }
        })
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

