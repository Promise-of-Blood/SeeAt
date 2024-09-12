package com.pob.seeat.presentation.view.detail

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.chip.Chip
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentDetailBinding
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.model.FeedReportModel
import com.pob.seeat.domain.model.toBookmarkEntity
import com.pob.seeat.presentation.view.chat.ChattingActivity
import com.pob.seeat.presentation.viewmodel.CommentViewModel
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import com.pob.seeat.presentation.viewmodel.ReportCommentViewModel
import com.pob.seeat.utils.EventBus
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime
import com.pob.seeat.utils.Utils.toTagList
import com.pob.seeat.utils.dialog.Dialog.showCommentDialog
import com.pob.seeat.utils.dialog.Dialog.showReportDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    val args: DetailFragmentArgs by navArgs()
    private lateinit var feed: FeedModel

    private val detailViewModel: DetailViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()
    private val reportCommentViewModel: ReportCommentViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val feedCommentAdapter: FeedCommentAdapter by lazy {
        FeedCommentAdapter(
            commentViewModel,
            ::handleClickFeed,
            ::onLongClicked
        )
    }

    private lateinit var chattingResultLauncher: ActivityResultLauncher<Intent>
    private var currentGeoPoint: GeoPoint = GeoPoint(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        initDetailViewmodel()
        (activity as MainActivity).setBottomNavigationVisibility(View.GONE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFeed()
        setupUI(view, binding.tvAddCommentButton)
        initCommentRecyclerView()
        Timber.i(args.feedIdArg)
        initCommentViewModel()
    }

    private fun initToolbar(feed: FeedModel) {
        (activity as AppCompatActivity).setSupportActionBar(binding.tbFeed)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.feed)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val reportedUserId = feed.user?.id
        val loginUserId = detailViewModel.uid

        if (reportedUserId == loginUserId) {
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_detail, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.feed_remove -> {

                            detailViewModel.removeFeed(feed.feedId)
                            Toast.makeText(requireContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT)
                                .show()

                            requireActivity().onBackPressedDispatcher.onBackPressed()

                            true
                        }

                        android.R.id.home -> {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                            true
                        }

                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        } else {
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_detail_another_user, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.report -> {
                            // 신고하기
                            if (reportedUserId != null) {
                                val reportFeed = FeedReportModel(
                                    loginUserId,
                                    reportedUserId,
                                    feed.feedId,
                                    Timestamp.now()
                                )
                                detailViewModel.addReportFeed(reportFeed)
                                Toast.makeText(
                                    requireContext(),
                                    "게시물이 신고되었습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "신고를 접수하는데 실패했습니다. 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            true
                        }

                        android.R.id.home -> {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                            true
                        }

                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }


    }

    private fun initDetailViewmodel() {
        detailViewModel.getFeedById(args.feedIdArg)

        initFeedLiked()
        initIsBookmarked()

    }

    private fun initFeedLiked() {
        val uid = detailViewModel.uid
        if (uid != null) {
            detailViewModel.getUserInfo(uid)
        } else {
            Timber.e("userUid", "uid 조회 실패")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.userInfo.collect { userInfo ->
                if (userInfo != null) {
                    Timber.i(userInfo.likedFeedList.toString())
                    detailViewModel.setIsLiked(args.feedIdArg in userInfo.likedFeedList)
                } else {
                    Timber.e("userInfo is null")
                }
            }
        }
    }

    private fun initCommentRecyclerView() {
        binding.rvCommentList.adapter = feedCommentAdapter
        binding.rvCommentList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getFeed() = with(detailViewModel) {

        viewLifecycleOwner.lifecycleScope.launch {
            singleFeedResponse.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { response ->
                    when (response) {
                        is Result.Error -> {
                            Timber.i("HomeFragment", "Error: ${response.message}")
                        }

                        is Result.Loading -> {
                            Timber.i("HomeFragment", "Loading..")
                        }

                        is Result.Success -> {
                            feed = response.data
                            Timber.i("HomeFragment", feed.toString())
                            initView(feed)
                            initToolbar(feed)

                        }
                    }
                }
        }
    }

    private fun initIsBookmarked() = with(detailViewModel) {
        getIsBookmarked(args.feedIdArg)
        handleBookmark(detailViewModel.isBookmarked.value)
        viewLifecycleOwner.lifecycleScope.launch {
            isBookmarked.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect { isBookmarked ->
                handleBookmark(isBookmarked)
                binding.clBookmarkBtn.setOnClickListener {
                    if (::feed.isInitialized) {
                        if (detailViewModel.isBookmarked.value) {
                            detailViewModel.deleteBookmark(feed.feedId)
                            Toast.makeText(requireContext(), "북마크를 삭제했습니다.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            detailViewModel.saveBookmark(feed.toBookmarkEntity())
                            Toast.makeText(requireContext(), "북마크에 추가했습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        getIsBookmarked(feed.feedId)
                    }
                }
            }
        }
    }

    private fun initView(feed: FeedModel) {

        Timber.i(feed.toString())
        binding.run {
            tvWriterUsername.text = feed.nickname

            Glide.with(requireContext())
                .load(feed.userImage)
                .into(ivWriterImage)

            tvFeedTitle.text = feed.title
            tvFeedTimeAgo.text = feed.date?.toLocalDateTime()?.toKoreanDiffString()
            tvFeedContent.text = feed.content
            tvFeedDetailLikeCount.text = feed.like.toString()
            tvCommentCount.text = feed.commentsCount.toString()

            initLocation()

            // 툴바 뒤로가기
            tbFeed.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            tvFeedGetPositionButton.setOnClickListener {
                Timber.d("tvFeedGetPositionButton Clicked")
                val bundle = Bundle().apply {
                    putFloat("feedLatitude", feed.location!!.latitude.toFloat()) // 전달할 위도
                    putFloat("feedLongitude", feed.location.longitude.toFloat()) // 전달할 경도
                }
                findNavController().navigate(R.id.action_detail_to_show_locate, bundle)
            }

            setFeedLikeButton(clLikeBtn)

            clLikeBtn.setOnClickListener {
                detailViewModel.isLikedToggle(args.feedIdArg)
                detailViewModel.modifyIsLiked(tvFeedDetailLikeCount.text.toString().toInt())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                EventBus.subscribe().collect { value ->
                    tvFeedDetailLikeCount.text = value.toString()
                }
            }

            tvAddCommentButton.setOnClickListener {
                val comment = binding.etAddComment.text.toString()
                Log.d("댓글달기", "etAddComment.text : ${etAddComment.text.toString()}")
                sendCommentToServer(comment)
                hideKeyboard()
            }




            tvChatButton.setOnClickListener {
                val intent = Intent(requireContext(), ChattingActivity::class.java)
                intent.putExtra("feedId", feed.feedId)
                startActivity(intent)
            }


            initTag(feed.tags)

            Timber.i(feed.contentImage.toString())
            if (feed.contentImage.isEmpty()) {
                vpDetailImages.visibility = View.GONE
                detailImageIndicator.visibility = View.GONE
            } else {
                initImageViewPager(feed.contentImage)
            }
        }
    }

    private fun initLocation() {

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        when (PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
            -> {
                requestFineLocation()
            }

            ActivityCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION)
            -> {
                requestCoarseLocation()
            }

            else -> {
                Timber.e("위치 권한이 없습니다.")
                hideDistance()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestFineLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).setMaxUpdates(1)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (_binding == null) return
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                        Timber.i("고정밀 위치: $currentGeoPoint")
                        feed.location?.let {
                            val distance = calculateDistance(currentGeoPoint, it)
                            binding.tvMyDistance.text = formatDistanceToString(distance)
                        }
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestCoarseLocation() {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            10000
        ).setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (_binding == null) return
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                        Timber.i("저정밀 위치: $currentGeoPoint")
                        feed.location?.let {
                            val distance = calculateDistance(currentGeoPoint, it)
                            binding.tvMyDistance.text = formatDistanceToString(distance)
                        }
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun hideDistance() {
        binding.tvMyDistance.visibility = View.GONE
        binding.viewDistanceDivider.visibility = View.GONE
        val marginInDp = 20
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, marginInDp.toFloat(), resources.displayMetrics
        ).toInt()

        val params = binding.tvFeedTimeAgo.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = marginInPx
        binding.tvFeedTimeAgo.layoutParams = params
    }

    private fun handleBookmark(isBookmarked: Boolean) = with(binding) {
        if (isBookmarked) {
            tvBookmarkBtnText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            ivBookmarkBtnIcon.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            ivBookmarkBtnIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_heart
                )
            )
            clBookmarkBtn.setBackgroundResource(R.drawable.round_r4_primary)
        } else {
            tvBookmarkBtnText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
            ivBookmarkBtnIcon.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.gray)
            ivBookmarkBtnIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_heart_outlined
                )
            )
            clBookmarkBtn.setBackgroundResource(R.drawable.round_r4_border)
        }
    }

    private fun changeDP(value: Int): Int {
        val displayMetrics = resources.displayMetrics
        val dp = Math.round(value * displayMetrics.density)
        return dp
    }

    private fun setLikeCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.isLiked.collect { isLiked ->
                val currentLikeCount =
                    binding.tvFeedDetailLikeCount.text.toString().toIntOrNull() ?: 0

                val newCount = if (isLiked) {
                    currentLikeCount + 1
                } else {
                    currentLikeCount - 1
                }

                binding.tvFeedDetailLikeCount.text = newCount.toString()

            }
        }
    }

    private fun setFeedLikeButton(clLikeBtn: ConstraintLayout) {
        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.isLiked.collect { isLiked ->
                when (isLiked) {
                    true -> {
                        clLikeBtn.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.round_r4_primary)
                    }

                    false -> {
                        clLikeBtn.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.round_r4_border)
                    }
                }
            }
        }
    }

    private fun initCommentViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.comments.collect { comments ->
                    feedCommentAdapter.submitList(comments.toList())
                    binding.tvCommentCount.text =
                        commentViewModel.comments.value.toList().size.toString()
                }
            }
        }

        commentViewModel.fetchComments(args.feedIdArg)
    }

    private fun initImageViewPager(contentImage: List<String>) {
        _binding?.apply {
            val imageViewPager = binding.vpDetailImages
            imageViewPager.adapter = ImagesPagerAdapter(contentImage)
            imageViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.detailImageIndicator.setViewPager(imageViewPager)
        }
    }

    private fun initTag(tags: List<String>) {
        val chipGroup = _binding?.chipsGroupDetail ?: return
        val tagLists = tags.toTagList()
        // tagList를 이용해 Chip을 동적으로 생성
        // tagLists:List<tag>

        for (tag in tagLists) {
            val chip = Chip(context).apply {
                text = tag.tagName
                setChipIconResource(tag.tagImage)

                chipBackgroundColor =
                    ContextCompat.getColorStateList(context, R.color.white)
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

    /**
     * 두 GeoPoint간의 거리 계산
     * @param myGeoPoint 현재 좌표 GeoPoint
     * @param feedGeoPoint 계산하려는 좌표 GeoPoint
     * @return 두 좌표간 거리가 미터단위로 Int로 반환됨
     */
    private fun calculateDistance(myGeoPoint: GeoPoint, feedGeoPoint: GeoPoint): Int {

        val RR = 6372.8 * 1000

        val myLatitude = myGeoPoint.latitude
        val myLongitude = myGeoPoint.longitude
        val feedLatitude = feedGeoPoint.latitude
        val feedLongitude = feedGeoPoint.longitude

        val dLat = Math.toRadians(feedLatitude - myLatitude)
        val dLon = Math.toRadians(feedLongitude - myLongitude)
        val a =
            sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(myLatitude))
        val c = 2 * asin(sqrt(a))

        return (RR * c).toInt()

    }

    private fun formatDistanceToString(meter: Int): String {
        return when {
            meter in 0..999 -> "${meter}m"
            meter > 1000 -> String.format(Locale.KOREA, "%dkm", meter / 1000)
            else -> "Invalid distance"
        }
    }

    private fun handleClickFeed(feedModel: CommentModel) {
        // Todo 댓글 클릭시
    }

    private fun onLongClicked(feedModel: CommentModel) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if (feedModel.user?.id == currentUid) {
            showCommentDialog(
                requireContext(),
                onDelete = {
                    commentViewModel.deleteComment(feedModel, requireContext())
                    commentViewModel.fetchComments(args.feedIdArg)
                }
            )
        } else {
            showReportDialog(
                requireContext(),
                onReport = {
                    val reportedUserUid = feedModel.user?.id
                    val timeStamp = Timestamp.now()
                    if (reportedUserUid != null) {
                        reportCommentViewModel.sendReport(
                            feedModel.user.id,
                            feedModel.feedId,
                            feedModel.commentId,
                            timeStamp
                        )
                        Toast.makeText(
                            requireContext(),
                            "신고가 정상적으로 접수 되었습니다. 감사합니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "신고를 접수하는데 실패했습니다. 다시 시도해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun sendCommentToServer(comment: String) {
        val feedId = args.feedIdArg
        val timeStamp = Timestamp.now()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userReference =
                FirebaseFirestore.getInstance().collection("user").document(currentUser.uid)
            userReference.get().addOnSuccessListener { snapShot ->
                if (snapShot.exists()) {
                    val userImage = snapShot.getString("profileUrl") ?: ""
                    val userNickname = snapShot.getString("nickname") ?: ""

                    commentViewModel.addComment(
                        feedId = feedId,
                        commentId = "",
                        comment = comment,
                        user = userReference,
                        likeCount = 0,
                        timeStamp = timeStamp,
                        userImage = userImage.toString(),
                        userNickname = userNickname
                    )
                    commentViewModel.fetchComments(feedId)
                    binding.etAddComment.setText("")


                    Log.d("코멘트사이즈", "${commentViewModel.comments.value.toList().size}")
                } else {
                    Log.e("댓글 달기", "댓글달기 실패! 사용자 문서 X")
                }

            }.addOnFailureListener { e ->
                Log.e("댓글 달기", "프로필 가져오기 실패! ${e.message} ")
            }


        } else {
            Log.e("댓글달기", "댓글 달기 실패! 다시시도하라!")

        }


    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus
        currentFocusView?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


    private fun setupUI(view: View, vararg exView: View) {
        // ViewGroup인 경우 자식들에게도 적용
        if (view !is EditText && !exView.contains(view)) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                view.clearFocus()
                false
            }
        }

        // ViewGroup일 경우 자식들에게도 setupUI 적용
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView, *exView)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
    }
}
