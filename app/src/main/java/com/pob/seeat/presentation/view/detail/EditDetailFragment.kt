package com.pob.seeat.presentation.view.detail

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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentEditDetailBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.model.TagModel
import com.pob.seeat.presentation.view.feed.ImageUploadAdapter
import com.pob.seeat.presentation.view.feed.NewFeedFragmentDirections
import com.pob.seeat.presentation.view.feed.NewFeedModalBottomSheet
import com.pob.seeat.presentation.view.feed.OnLocationSelectedListener
import com.pob.seeat.presentation.view.feed.SelectLocateFragment
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.Utils.compressBitmapToUri
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.resizeImage
import com.pob.seeat.utils.Utils.tagList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class EditDetailFragment : Fragment(), OnLocationSelectedListener {
    private var _binding: FragmentEditDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by activityViewModels()
    private val newFeedViewModel: NewFeedViewModel by activityViewModels()

    private lateinit var selectedMap: NaverMap

    private var selectedTagList = emptyList<TagModel>()
    private var selectLocation: LatLng? = null

    private val uid = getUserUid()

    private lateinit var adapter: ImageUploadAdapter
    private lateinit var multipleImagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private val uriList = mutableListOf<Uri>()
    private var imageCount = 5
    private var isSetBeforeLocation = false

    override fun onLocationSelected(location: LatLng) {
        Timber.d("onLocationSelected: $location")
        binding.apply {
            map.visibility = View.VISIBLE
            ivMarker.visibility = View.VISIBLE
            ivMarkerShadow.visibility = View.VISIBLE
        }

        if (::selectedMap.isInitialized) {
            val cameraUpdate = CameraUpdate.scrollTo(location)
            selectedMap.moveCamera(cameraUpdate)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setImagePicker()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).setBottomNavigationVisibility(View.GONE)
        _binding = FragmentEditDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadFeedData()
        initDetailNaverMap()
        initialSetting()
//        setSelectLocation()
        Timber.i("onViewCreated")
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

        Timber.i("selectLocation ViewModel ${newFeedViewModel.selectLocation}")
        setSelectLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NewfeedFragment", "onDestroy")
        newFeedViewModel.selectLocation = null
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
        _binding = null
    }


    private fun initLoadFeedData() {
        val feedModel = detailViewModel.singleFeedResponse.value
        binding.apply {
            when (feedModel) {
                is Result.Success -> {
                    etTitle.setText(feedModel.data.title)
                    etContent.setText(feedModel.data.content)

                    chipGroup.removeAllViews()
                    selectedTagList = tagList.filter { tagModel ->
                        feedModel.data.tags.contains(tagModel.tagName)
                    }
                    newFeedViewModel.updateSelectTagList(selectedTagList)
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }
    }

    private fun setImagePicker() {
        // TODO PickMultipleVisualMedia 에서 선택 갯수 제한은 API 33부터 지원, 수정 필요
        // ActivityResultLauncher를 미리 등록
        multipleImagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(imageCount)) { uris ->
                Timber.i(uris.toString())
                if (uris.isNotEmpty()) {
                    uris.forEach { uri ->
                        try {

                            val resizedBitmap = resizeImage(requireContext(), uri)

                            resizedBitmap?.let {
                                // Bitmap을 파일로 저장하고 Uri로 변환
                                val resizedUri = compressBitmapToUri(requireContext(), it)

                                resizedUri?.let { uri ->
                                    uriList.add(uri)
                                }
                            } ?: Timber.e("Uri is not found $uri")
                        } catch (e: Exception) {
                            Timber.e(e, "image exception $uri")
                            Toast.makeText(requireContext(), "이미지 처리중 오류", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }

                    // 이미지가 5개를 넘을 경우 초과된 부분 삭제
                    if (uriList.size > 5) {
                        val excess = uriList.size - 5
                        repeat(excess) {
                            uriList.removeLast()
                        }
                        Toast.makeText(
                            requireContext(),
                            "최대 5개의 이미지만 선택 가능합니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    imageCount -= uris.size
                    adapter.submitList(uriList.toList())
                } else {
                    Toast.makeText(requireContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setSelectLocation() {
        Timber.tag("NewfeedFragment").d("Start setSelectLocation")

        binding.apply {

            // TODO Resume 시 네이버 맵의 초기 좌표값을 selectLocation으로 설정
            if (newFeedViewModel.selectLocation != null) {
                tvMap.visibility = View.INVISIBLE
                map.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    // 5초 대기
                    delay(5000)
                    // NaverMap의 초기 카메라 위치를 설정 (카메라 이동 애니메이션 없이)
                    Timber.i("selectLocation coroutine ${newFeedViewModel.selectLocation}")
                    newFeedViewModel.selectLocation?.let {
                        val cameraPosition = CameraPosition(newFeedViewModel.selectLocation!!, 16.0)
                        selectedMap.cameraPosition = cameraPosition
                    }
                }
            } else {
                Timber.tag("NewfeedFragment")
                    .d("selectLocation is null: ${newFeedViewModel.selectLocation}")
                tvMap.visibility = View.VISIBLE
                map.visibility = View.INVISIBLE
            }
        }
    }


    private fun initDetailNaverMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        Timber.tag("isSetBeforeLocation").i(isSetBeforeLocation.toString())
        if (!isSetBeforeLocation) {
            val feedModel = detailViewModel.singleFeedResponse.value

            when (feedModel) {
                is Result.Success -> {
                    val geoPoint = feedModel.data.location
                    if (geoPoint != null) {
                        newFeedViewModel.updateSelectLocation(
                            LatLng(
                                geoPoint.latitude,
                                geoPoint.longitude
                            )
                        )


                        Timber.tag("init location").i(selectLocation.toString())
                    }
                }

                is Result.Error -> TODO()
                is Result.Loading -> TODO()
            }
            isSetBeforeLocation = true
        }


        // StateFlow로 naverMap 객체를 구독하여 값이 설정되면 작업 처리
        mapFragment.getMapAsync { naverMap ->
            selectedMap = naverMap

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
                binding.cvEditSelectLocationFragment.findViewById<View>(R.id.cl_select_location).visibility =
                    View.VISIBLE
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

            val feedModel = detailViewModel.singleFeedResponse.value

            when (feedModel) {
                is Result.Success -> {
                    val bundle = Bundle().apply {
                        feedModel.data.location?.latitude?.let { it1 ->
                            putFloat(
                                "homeLatitude",
                                it1.toFloat()
                            )
                        }  // 예시 값
                        feedModel.data.location?.longitude?.let { it1 ->
                            putFloat(
                                "homeLongitude",
                                it1.toFloat()
                            )
                        }  // 예시 값
                        putFloat("homeZoom", 14F)  // 예시 값
                    }
                    Timber.i("bundel = $bundle")

                    val selectLocationFragment = SelectLocateFragment().apply {
                        arguments = bundle
                    }

                    Timber.tag("editDetailFragment")
                        .i(" arguments of selctLocationFragment = ${selectLocationFragment.arguments}")
                    childFragmentManager.beginTransaction()
                        .replace(R.id.cv_edit_select_location_fragment, selectLocationFragment)
                        .commit()
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }

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
//                if (binding.map.visibility == View.VISIBLE) {
//                    binding.map.visibility = View.INVISIBLE
//                }
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
            newFeedViewModel.selectTagList.collect { tagList ->
                selectedTagList = tagList
                updateSelectedTag()
            }
        }
    }

    private fun uploadFeed() {
        val beforeFeed = detailViewModel.singleFeedResponse.value


        if (checkException()) {
            Timber.tag("NewFeed").d("Upload Feed")

            // 업로드 중에는 ProgressBar를 표시
            binding.clProgress.visibility = View.VISIBLE

            when (beforeFeed) {
                is Result.Success -> {

                    newFeedViewModel.uploadFeedImageList(uriList, beforeFeed.data.feedId)

                    viewLifecycleOwner.lifecycleScope.launch {
                        newFeedViewModel.feedImageUploadResult.collect { result ->
                            when (result) {
                                "SUCCESS" -> {
                                    Log.d(
                                        "NewFeedFragment",
                                        "contentImage: ${newFeedViewModel.feedImageList}"
                                    )


                                    // 피드 데이터를 업로드
                                    val tagNameList = if (selectedTagList.isEmpty()) {
                                        listOf("기타")
                                    } else {
                                        selectedTagList.map { it.tagName }
                                    }

                                    val feedModel = FeedModel(
                                        feedId = beforeFeed.data.feedId,
                                        user = beforeFeed.data.user,
                                        nickname = beforeFeed.data.nickname,
                                        title = binding.etTitle.text.toString(),
                                        content = binding.etContent.text.toString(),
                                        like = beforeFeed.data.like,
                                        commentsCount = beforeFeed.data.commentsCount,
                                        location = newFeedViewModel.selectLocation?.let {
                                            GeoPoint(
                                                it.latitude,
                                                it.longitude
                                            )
                                        },
                                        comments = beforeFeed.data.comments,
                                        tags = tagNameList,
                                        userImage = beforeFeed.data.userImage,
                                        contentImage = newFeedViewModel.feedImageList
                                    )
                                    Timber.tag("editFeedRemote").i("editFragment: $feedModel")

                                    detailViewModel.editFeed(feedModel)


                                    // 업로드 완료 후 ProgressBar를 숨김
                                    binding.clProgress.visibility = View.GONE
                                    Toast.makeText(context, "수정 성공", Toast.LENGTH_SHORT).show()
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

                is Result.Error -> Timber.i("이전 디테일 페이지 데이터 에러")
                Result.Loading -> Timber.i("이전 디테일 페이지 데이터 로딩중")
            }


            // ViewModel에서 이미지 업로드 결과를 확인

        }
    }

    private fun checkException(): Boolean {
        // title, content, selectLocation이 모두 유효한지 검사
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val location = newFeedViewModel.selectLocation

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