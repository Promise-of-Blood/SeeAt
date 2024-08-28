package com.pob.seeat.presentation.view.home

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.messaging.FirebaseMessaging
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.map.MapFragment
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
            rvTagList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvTagList.adapter = adapter

            val marginDecoration = MarginItemDecoration(24) // 16dp 마진
            rvTagList.addItemDecoration(marginDecoration)
        }
    }
}

class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                left = spaceHeight // 첫 번째 아이템에는 왼쪽 마진을 추가
            }
            right = spaceHeight // 오른쪽 마진을 모든 아이템에 추가
        }
    }
}