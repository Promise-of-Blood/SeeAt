package com.pob.seeat.presentation.view.detail

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
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
import com.pob.seeat.presentation.view.chat.ChattingActivity
import com.pob.seeat.presentation.viewmodel.CommentViewModel
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import com.pob.seeat.utils.EventBus
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime
import com.pob.seeat.utils.Utils.toTagList
import com.pob.seeat.utils.dialog.Dialog.showCommentDialog
import com.pob.seeat.utils.dialog.Dialog.showReportDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val detailViewModel: DetailViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()

    private val feedCommentAdapter: FeedCommentAdapter by lazy {
        FeedCommentAdapter(
            commentViewModel,
            ::handleClickFeed,
            ::onLongClicked
        )
    }

    private lateinit var chattingResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chattingResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    findNavController().navigate(R.id.navigation_home)
                    (activity as MainActivity).setBottomNavigationSelectedItem(R.id.navigation_message)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        initDetailViewmodel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFeed()
//        initTagRecyclerView()

        setupUI(view, binding.tvAddCommentButton)
        initCommentRecyclerView()
        Timber.i(args.feedIdArg)
        initCommentViewModel()
    }

    private fun initDetailViewmodel() {
        detailViewModel.getFeedById(args.feedIdArg)

        initFeedLiked()


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
                            val feed = response.data
                            Timber.i("HomeFragment", feed.toString())
                            initView(feed)
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

            // Todo 나의 위치 가져오기
            val myLatitude = 37.570201
            val myLongitude = 126.976879
            val geoPoint = GeoPoint(myLatitude, myLongitude)
            feed.location?.let {
                val distance = calculateDistance(geoPoint, it)
                tvMyDistance.text = formatDistanceToString(distance)
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


            clBookmarkBtn.setOnClickListener {
                // Todo 북마크 누를 때
            }

            tvFeedGetPositionButton.setOnClickListener {
                // Todo 위치보기
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
                chattingResultLauncher.launch(intent)
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
                }
            }
        }

        commentViewModel.fetchComments(args.feedIdArg)
    }

    private fun initImageViewPager(contentImage: List<String>) {
        val imageViewPager = binding.vpDetailImages
        imageViewPager.adapter = ImagesPagerAdapter(contentImage)
        imageViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.detailImageIndicator.setViewPager(imageViewPager)
    }

    private fun initTag(tags: List<String>) {
        val chipGroup = binding.chipsGroupDetail
        val tagLists = tags.toTagList()
        // tagList를 이용해 Chip을 동적으로 생성
        // tagLists:List<tag>

        for (tag in tagLists) {
            val chip = Chip(context).apply {
                text = tag.tagName
                setChipIconResource(tag.tagImage)

                chipBackgroundColor =
                    ContextCompat.getColorStateList(context, R.color.background_gray)
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
        if(feedModel.user?.id == currentUid){
            showCommentDialog(
                requireContext(),
                onDelete = {
                    commentViewModel.deleteComment(feedModel)
                    commentViewModel.fetchComments(args.feedIdArg)
                },
                onEdit = {
                    Toast.makeText(requireContext(), "미구현된 기능입니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }else{
            showReportDialog(
                requireContext(),
                onReport = {
                    Toast.makeText(requireContext(), "미구현된 기능입니다.", Toast.LENGTH_SHORT).show()
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
}
