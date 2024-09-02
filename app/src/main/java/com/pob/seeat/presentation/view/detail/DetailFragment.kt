package com.pob.seeat.presentation.view.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentDetailBinding
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import com.pob.seeat.presentation.viewmodel.HomeViewModel
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    val args: DetailFragmentArgs by navArgs()

    private val detailViewModel: DetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        detailViewModel.getFeedById(args.feedIdArg)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFeed()
        Timber.i(args.feedIdArg)
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
                            binding.run {
                                tvWriterUsername.text = feed.nickname
                                tvFeedTitle.text = feed.title
                                //todo 게시글과 내거리차이 계산
                                //todo 이미지 연결
                                //todo tag생성
                                tvFeedTimeAgo.text = feed.date?.toLocalDateTime()?.toKoreanDiffString()
                                tvFeedContent.text = feed.content
                                tvFeedDetailLikeCount.text = feed.like.toString()
                                tvCommentCount.text = feed.commentsCount.toString()
                            }
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}