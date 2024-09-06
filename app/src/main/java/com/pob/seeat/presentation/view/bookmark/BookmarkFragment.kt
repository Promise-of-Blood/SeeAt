package com.pob.seeat.presentation.view.bookmark

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentBookmarkBinding
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.presentation.viewmodel.BookmarkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val bookmarkAdapter: BookmarkAdapter by lazy { BookmarkAdapter(::handleClickBookmark) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 디테일 화면 진입 후, 다른 화면으로 이동했다가 다시 돌아오면 북마크 화면으로 이동
        // (디테일 화면 pop)
        findNavController().popBackStack(R.id.navigation_bookmark, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        bookmarkViewModel.getBookmarkList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        rvBookmark.adapter = bookmarkAdapter
        rvBookmark.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initViewModel() = with(bookmarkViewModel) {
        getBookmarkList()
        viewLifecycleOwner.lifecycleScope.launch {
            bookmarkList.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { response ->
                when (response) {
                    is Result.Error -> {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Result.Loading -> {
                        binding.rvBookmark.visibility = View.INVISIBLE
                    }

                    is Result.Success -> {
                        binding.rvBookmark.visibility = View.VISIBLE
                        bookmarkAdapter.submitList(response.data)
                    }
                }
            }
        }
    }

    private fun handleClickBookmark(feed: BookmarkModel) {
        val action = BookmarkFragmentDirections.actionBookmarkToDetail(feed.feedId)
        findNavController().navigate(action)
    }
}