package com.pob.seeat.presentation.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naver.maps.map.MapFragment
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding

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
    }
}