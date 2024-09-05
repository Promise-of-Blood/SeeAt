package com.pob.seeat.presentation.view.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding
import com.pob.seeat.databinding.FragmentNewFeedBinding
import com.pob.seeat.presentation.view.home.TagAdapter
import com.pob.seeat.utils.Utils.tagList
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestoreSettings
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.pob.seeat.MainActivity
import com.pob.seeat.data.repository.NaverMapWrapper
import com.pob.seeat.domain.model.TagModel
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import com.pob.seeat.utils.Utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class NewFeedFragment : Fragment() {
    private var _binding: FragmentNewFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewFeedViewModel by activityViewModels()

    private lateinit var selectedMap: NaverMap

    @Inject
    lateinit var naverMapWrapper: NaverMapWrapper

    private var selectedTagList = emptyList<TagModel>()
    private var selectLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).setBottomNavigationVisibility(View.GONE)
        _binding = FragmentNewFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNaverMap()
        initialSetting()
        uploadFeed()
    }

    private fun initNaverMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        naverMapWrapper.initialize(mapFragment)

        // StateFlow로 naverMap 객체를 구독하여 값이 설정되면 작업 처리
        lifecycleScope.launchWhenResumed {
            naverMapWrapper.getNaverMap().collect { naverMap ->
                selectedMap = naverMap!!

                selectedMap.isIndoorEnabled = true

                val uiSettings = selectedMap.uiSettings
                uiSettings.apply {
                    isLocationButtonEnabled = false
                    isCompassEnabled = false
                    isZoomControlEnabled = false
                    isTiltGesturesEnabled = false
                    isScaleBarEnabled = false
                    isZoomGesturesEnabled = false
                    isScrollGesturesEnabled = false
                    isIndoorLevelPickerEnabled = false
                }

                selectedMap.setOnMapClickListener { point, coord ->
                    findNavController().navigate(R.id.action_new_feed_to_select_locate)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.tag("NewfeedFragment").d("onPause")
    }

    override fun onResume() {
        super.onResume()
        Timber.tag("NewfeedFragment").d("onResume")

        if (::selectedMap.isInitialized) {
            Timber.tag("NewfeedFragment").d("map is initialized")
            selectLocation = viewModel.selectLocation
            setSelectLocation() // selectedMap이 초기화된 후에 위치 설정
        }
    }


    private fun setSelectLocation() {
        Timber.tag("NewfeedFragment").d("Start setSelectLocation")
        binding.apply {

            // TODO Resume 시 네이버 맵의 초기 좌표값을 selectLocation으로 설정
            if (selectLocation != null) {
                Timber.tag("NewfeedFragment").d("selectLocation is Not null: $selectLocation")
                tvMap.visibility = View.INVISIBLE
                map.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    // 5초 대기
                    delay(5000)
                    // NaverMap의 초기 카메라 위치를 설정 (카메라 이동 애니메이션 없이)
                    val cameraPosition = CameraPosition(selectLocation!!, 16.0)
                    selectedMap.cameraPosition = cameraPosition
                }
            } else {
                Timber.tag("NewfeedFragment").d("selectLocation is null: $selectLocation")
                tvMap.visibility = View.VISIBLE
                map.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NewfeedFragment", "onDestroy")
        viewModel.selectLocation = null
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
        _binding = null
    }

    private fun updateSelectedTag() {
        binding.apply {
            // ChipGroup 초기화 (기존 Chip 제거)
            chipGroup.removeAllViews()

            if (selectedTagList.isEmpty()) {
                tvSelectTag.text = "태그를 선택해 주세요"
            } else {
                tvSelectTag.text = ""
                // tagList를 이용해 Chip을 동적으로 생성
                Timber.tag("NewFeedFragment").d("selectedTagList: $selectedTagList")
                for (tag in selectedTagList) {
                    val chip = Chip(context).apply {
                        text = tag.tagName
                        setChipIconResource(tag.tagImage)

                        chipBackgroundColor = ContextCompat.getColorStateList(
                            context,
                            R.color.background_chip_background_selector
                        )
                        chipStrokeWidth = 0f
                        chipIconSize = 16f.px.toFloat()
                        chipCornerRadius = 32f.px.toFloat()
                        chipStartPadding = 10f.px.toFloat()

                        elevation = 2f.px.toFloat()

                        isCheckable = false
                        isClickable = false
                    }

                    // ChipGroup에 동적으로 Chip 추가
                    chipGroup.addView(chip)
                }
            }
        }
    }

    private fun initialSetting() {
        binding.apply {
            tvMap.setOnClickListener {
                findNavController().navigate(R.id.action_new_feed_to_select_locate)
            }

            tvSelectTag.setOnClickListener {
                val modal = NewFeedModalBottomSheet()
                modal.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.RoundCornerBottomSheetDialogTheme
                )
                modal.show(parentFragmentManager, modal.tag)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.selectTagList.collect { tagList ->
                // selectTagList의 변화가 생기면 실행되는 코드
                selectedTagList = tagList
                selectedTagList.forEach {
                    Timber.tag("NewFeedFragment").d("Selected tags: " + it.tagName)
                }
                updateSelectedTag()
            }
        }
    }

    private fun uploadFeed() {
        binding.apply {
            Timber.tag("NewFeed").d("Upload Feed")
            tvUploadFeed.setOnClickListener {
                val firestore = FirebaseFirestore.getInstance()

                val tagNameList = selectedTagList.map { it.tagName }
                val currentUser = FirebaseAuth.getInstance().currentUser

                var userDocRef: DocumentReference = FirebaseFirestore.getInstance()
                    .collection("user")
                    .document("4Y4GoAwt6yTlXYbeGzTbQTea9rq2")

                currentUser?.let { user ->
                    val uid = user.uid

                    userDocRef = FirebaseFirestore.getInstance()
                        .collection("user")
                        .document(uid)
                }

                val feedData: HashMap<String, Any> = hashMapOf(
                    "title" to etTitle.text.toString(),
                    "content" to etContent.text.toString(),
                    "date" to Timestamp(Date()),
                    "tagList" to tagNameList,
                    "location" to GeoPoint(selectLocation!!.latitude, selectLocation!!.longitude),
                    "like" to 0,
                    "commentsCount" to 0,
                    "user" to userDocRef
                )

                val db = Firebase.firestore
                Timber.tag("NewFeed").d("send firestore")
                db
                    .collection("feed")
                    .document()
                    .set(feedData)
                    .addOnSuccessListener { documentReference ->
                        // 성공 시 처리
                        Timber.d("DocumentSnapshot added with ID: $documentReference")
                        Toast.makeText(context, "업로드 성공", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }
                    .addOnFailureListener { e ->
                        // 실패 시 처리
                        Timber.w(e, "Error adding document")
                        Toast.makeText(context, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}