package com.pob.seeat.presentation.view.feed

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentNewFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.pob.seeat.MainActivity
import com.pob.seeat.domain.model.TagModel
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.Utils.compressBitmapToUri
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.resizeImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@AndroidEntryPoint
class NewFeedFragment : Fragment() {
    private var _binding: FragmentNewFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewFeedViewModel by activityViewModels()

    private lateinit var selectedMap: NaverMap

    private var selectedTagList = emptyList<TagModel>()
    private var selectLocation: LatLng? = null

    private val uid = getUserUid()

    private lateinit var adapter: ImageUploadAdapter
    private lateinit var multipleImagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private val uriList = mutableListOf<Uri>()
    private var imageCount = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO PickMultipleVisualMedia 에서 선택 갯수 제한은 API 33부터 지원, 수정 필요
        // ActivityResultLauncher를 미리 등록
        multipleImagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(imageCount)) { uris ->
                if (uris.isNotEmpty()) {
                    uris.forEach { uri ->
                        val resizedBitmap = resizeImage(requireContext(), uri)

                        resizedBitmap.let {
                            // Bitmap을 파일로 저장하고 Uri로 변환
                            val resizedUri = compressBitmapToUri(requireContext(), it)

                            resizedUri.let { uri ->
                                uriList.add(uri)
                            }
                        }
                    }

                    // 이미지가 5개를 넘을 경우 초과된 부분 삭제
                    if (uriList.size > 5) {
                        val excess = uriList.size - 5
                        repeat(excess) {
                            uriList.removeLast()
                        }
                        Toast.makeText(requireContext(), "최대 5개의 이미지만 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                    }

                    imageCount -= uris.size
                    adapter.submitList(uriList.toList())
                } else {
                    Toast.makeText(requireContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }
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
    }


    override fun onPause() {
        super.onPause()
        Timber.tag("NewfeedFragment").d("onPause")
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        Timber.tag("NewfeedFragment").d("onResume")

        // 좌표 선택 후 돌아오면 선택한 좌표값 가져오기
        if (::selectedMap.isInitialized) {
            Timber.tag("NewfeedFragment").d("map is initialized")
            selectLocation = viewModel.selectLocation
            setSelectLocation() // selectedMap이 초기화된 후에 위치 설정
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


    private fun initNaverMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment

        // StateFlow로 naverMap 객체를 구독하여 값이 설정되면 작업 처리
        mapFragment.getMapAsync { naverMap ->
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

            toolbarMessage.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            // RecyclerView를 가로 스크롤 가능하게 설정
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvImageList.layoutManager = layoutManager

            adapter = ImageUploadAdapter({ uri ->
                val uriString: String = uri.toString()
                // 이미지 클릭 시 다이얼로그를 호출하여 이미지를 크게 보여줍니다.
                val action = NewFeedFragmentDirections.actionNewFeedToDetailImage(uriString)
                findNavController().navigate(action)
            }) { position ->
                // 삭제 콜백 처리 - 이미지가 삭제될 때 imageCount 증가
                uriList.removeAt(position)
                adapter.submitList(uriList.toList())
                imageCount += 1
            }

            binding.rvImageList.adapter = adapter

            // 현재 선택된 이미지 리스트를 전달
            adapter.submitList(uriList.toList())

            ivUploadImage.setOnClickListener {
                if (imageCount > 0) {
                    // 이미지 선택기를 실행하고, 남은 개수만큼 선택할 수 있도록 제한
                    multipleImagePickerLauncher.launch(PickVisualMediaRequest().apply {})
                } else {
                    Toast.makeText(requireContext(), "최대 5개의 이미지를 선택할 수 있습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

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

            tvUploadFeed.setOnClickListener {
                uploadFeed()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.selectTagList.collect { tagList ->
                selectedTagList = tagList
                updateSelectedTag()
            }
        }
    }

    private fun uploadFeed() {
        if (checkException()) {
            Timber.tag("NewFeed").d("Upload Feed")
            val firestore = FirebaseFirestore.getInstance()

            // 새로운 피드 ID 생성
            val feedId = firestore.collection("feed").document().id

            // 태그 리스트 생성
            val tagNameList = if (selectedTagList.isEmpty()) {
                listOf("기타")
            } else {
                selectedTagList.map { it.tagName }
            }

            val userDocRef: DocumentReference = firestore
                .collection("user")
                .document(uid!!)

            // 업로드 중에는 ProgressBar를 표시
            binding.clProgress.visibility = View.VISIBLE

            // 이미지 업로드 후 처리
            viewModel.uploadFeedImageList(uriList, feedId)

            // ViewModel에서 이미지 업로드 결과를 확인
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.feedImageUploadResult.collect { result ->
                    when (result) {
                        "SUCCESS" -> {
                            Log.d("NewFeedFragment", "contentImage: ${viewModel.feedImageList}")
                            // 이미지 업로드가 성공했을 때, contentImage 필드에 추가
                            val feedData: HashMap<String, Any> = hashMapOf(
                                "title" to binding.etTitle.text.toString(),
                                "content" to binding.etContent.text.toString(),
                                "date" to Timestamp(Date()),
                                "tagList" to tagNameList,
                                "location" to GeoPoint(selectLocation!!.latitude, selectLocation!!.longitude),
                                "like" to 0,
                                "commentsCount" to 0,
                                "user" to userDocRef,
                                "contentImage" to viewModel.feedImageList // 이미지 리스트 추가
                            )

                            // 피드 데이터를 업로드
                            viewModel.uploadFeed(feedData, feedId)

                            // 업로드 완료 후 ProgressBar를 숨김
                            binding.clProgress.visibility = View.GONE
                            Toast.makeText(context, "업로드 성공", Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        }
                        "ERROR" -> {
                            // 업로드 실패 시 ProgressBar 숨김 및 오류 메시지 출력
                            binding.clProgress.visibility = View.GONE
                            Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                        "LOADING" -> {
                            // 업로드 중에는 ProgressBar를 계속 표시
                            binding.clProgress.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun checkException(): Boolean {
        // title, content, selectLocation이 모두 유효한지 검사
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val location = selectLocation

        // 제목이 비어 있을 때
        if (uid == null) {
            Toast.makeText(context, "유저 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        // 제목이 비어 있을 때
        if (title.isEmpty()) {
            Toast.makeText(context, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return false
        }

        // 내용이 비어 있을 때
        if (content.isEmpty()) {
            Toast.makeText(context, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return false
        }

        // 위치가 선택되지 않았을 때
        if (location == null) {
            Toast.makeText(context, "위치를 선택해 주세요", Toast.LENGTH_SHORT).show()
            return false
        }

        // 모든 조건이 만족되면 true 반환
        return true
    }

}