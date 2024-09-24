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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
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
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.pob.seeat.MainActivity
import com.pob.seeat.domain.model.TagModel
import com.pob.seeat.presentation.viewmodel.NewFeedViewModel
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.Utils.compressBitmapToUri
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.resizeImage
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@AndroidEntryPoint
class NewFeedFragment : Fragment(), OnLocationSelectedListener {

    private var _binding: FragmentNewFeedBinding? = null
    private val binding get() = _binding!!

    private val args: NewFeedFragmentArgs by navArgs()

    private val viewModel: NewFeedViewModel by activityViewModels()

    private lateinit var selectedMap: NaverMap

    private var selectedTagList = emptyList<TagModel>()
    private var selectLocation: LatLng? = null

    private val uid = getUserUid()

    private lateinit var adapter: ImageUploadAdapter
    private lateinit var multipleImagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private val uriList = mutableListOf<Uri>()
    private var imageCount = 5

    override fun onLocationSelected(location: LatLng) {
        Timber.d("onLocationSelected: $location")
        binding.apply {
            map.visibility = View.VISIBLE
            ivMarker.visibility = View.VISIBLE
            ivMarkerShadow.visibility = View.VISIBLE

            binding.clSelectLocate.bringToFront()
            binding.selectLocationFragment.bringToFront()
        }

        if (::selectedMap.isInitialized) {
            val cameraUpdate = CameraUpdate.scrollTo(location)
            selectedMap.moveCamera(cameraUpdate)
        } else {
            initNaverMap(location)
        }
    }

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
        initialSetting()
    }


    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        viewModel.selectLocation = null
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
        _binding = null
    }

    private fun initNaverMap(location: LatLng) {
        selectLocation = location

        val options = NaverMapOptions()
            .camera(CameraPosition(location, 16.0))

        val mapFragment = MapFragment.newInstance(options).also {
            childFragmentManager.beginTransaction().add(R.id.map, it).commit()
        }

        // StateFlow로 naverMap 객체를 구독하여 값이 설정되면 작업 처리
        mapFragment.getMapAsync { naverMap ->
            Timber.d("NaverMap Async")
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
        }

    }

    private fun updateSelectedTag() {
        binding.apply {
            // ChipGroup 초기화 (기존 Chip 제거)
            chipGroup.removeAllViews()

            // tagList를 이용해 Chip을 동적으로 생성
            Timber.d("selectedTagList: $selectedTagList")
            for (tag in selectedTagList) {
                val chip = Chip(context).apply {
                    text = tag.tagName
                    setChipIconResource(tag.tagImage)

                    chipBackgroundColor =
                        AppCompatResources.getColorStateList(context, R.color.white)

                    chipStrokeWidth = 1f
                    chipStrokeColor = ContextCompat.getColorStateList(context, R.color.gray)
                    chipIconSize = 16f.px.toFloat()
                    chipCornerRadius = 32f.px.toFloat()
                    chipStartPadding = 10f.px.toFloat()

                    rippleColor = AppCompatResources.getColorStateList(context, R.color.transparent)

                    isCheckable = false
                    isClickable = false
                }

                // ChipGroup에 동적으로 Chip 추가
                chipGroup.addView(chip)
            }

        }
    }

    private fun initialSetting() {
        binding.apply {
            val bundle = Bundle().apply {
                putFloat("homeLatitude", args.homeLatitude)  // 예시 값
                putFloat("homeLongitude", args.homeLongitude)  // 예시 값
                putFloat("homeZoom", args.homeZoom)  // 예시 값
            }

            val selectLocationFragment = SelectLocateFragment().apply {
                arguments = bundle
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.select_location_fragment, selectLocationFragment)
                .commit()

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

            clSelectLocate.setOnClickListener {
                binding.selectLocationFragment.findViewById<View>(R.id.cl_select_location).visibility =
                    View.VISIBLE
                if (binding.map.visibility == View.VISIBLE) {
                    binding.map.visibility = View.INVISIBLE
                }
            }

            llTag.setOnClickListener {
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

                            val lat = selectLocation!!.latitude
                            val lng = selectLocation!!.longitude

                            val geohash = GeoFireUtils.getGeoHashForLocation(
                                GeoLocation(lat, lng)
                            )
                            // 이미지 업로드가 성공했을 때, contentImage 필드에 추가
                            val feedData: HashMap<String, Any> = hashMapOf(
                                "title" to binding.etTitle.text.toString(),
                                "content" to binding.etContent.text.toString(),
                                "date" to Timestamp(Date()),
                                "tagList" to tagNameList,
                                "location" to GeoPoint(lat, lng),
                                "geohash" to geohash,
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