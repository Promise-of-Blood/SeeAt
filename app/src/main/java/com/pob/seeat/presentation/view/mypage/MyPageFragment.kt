package com.pob.seeat.presentation.view.mypage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.storage.FirebaseStorage
import com.pob.seeat.BuildConfig
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentMyPageBinding
import com.pob.seeat.presentation.view.admin.AdminActivity
import com.pob.seeat.presentation.view.mypage.settings.SettingsActivity
import com.pob.seeat.presentation.viewmodel.MyPageSharedViewModel
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.Utils.toFormatShortenedString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserInfoViewModel by viewModels()
    private val mypageSharedViewModel: MyPageSharedViewModel by activityViewModels()
    val uid = getUserUid()

    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val updatedNickname = data.getStringExtra("updatedNickname")
                val updatedIntroduce = data.getStringExtra("updatedIntroduce")
                val updatedProfileUrl = data.getStringExtra("updatedProfileUrl")
                Log.d("공명선", "$updatedProfileUrl")

                if (updatedNickname != null && updatedIntroduce != null) {

                    binding.tvUserName.text = updatedNickname
                    binding.tvUserIntroduce.text = updatedIntroduce
                    displayImage(updatedProfileUrl)

                    userViewModel.getUserInfo(uid!!)
                } else {
                    Log.e("MyPageFragment", "업데이트된 데이터가 없습니다.")
                }
            } else {
                Log.e("MyPageFragment", "결과 데이터가 null입니다.")
            }
            refreshUserInfo()
        } else {
            Toast.makeText(requireContext(), "프로필 업데이트 취소", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeUserInfo()
    }

    override fun onResume() {
        super.onResume()
        // Fragment가 다시 보일 때마다 최신 데이터를 가져옴
        refreshUserInfo()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        vpMyPageHistory.adapter = ViewPagerAdapter(requireActivity())
        TabLayoutMediator(tlMyPageHistory, vpMyPageHistory) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.history_feed)
                1 -> tab.text = getString(R.string.history_comment)
                2 -> tab.text = getString(R.string.history_liked_feed)
            }
        }.attach()


        if (uid != null) {
            userViewModel.getUserInfo(uid)
        } else {
            Log.e("userUid", "uid 조회 실패")
        }

        btnMyPage.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }

        mbMyPageTerms.setOnClickListener {
            findNavController().navigate(R.id.action_my_page_to_terms_of_service)
        }
        mbMyPageOss.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.my_page_oss_license))
        }

        tvMyPageVersion.text = getString(R.string.my_page_version, BuildConfig.VERSION_NAME)

        mbMyPageSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        mbMyPageLogout.setOnClickListener {
            GoogleAuthUtil.googleLogout(requireActivity())
        }

        mbMyPageAdmin.setOnClickListener {
            startActivity(Intent(requireContext(), AdminActivity::class.java))
        }

        tvMyPageVersion.setOnClickListener {
            when ((easterEgg) % 10) {
                7 -> toast("IDKOS : 난 너무 잘생겼어")
                8 -> toast("IDKOS : 날 보면 볼수록 너무 좋아")
                9 -> toast("IDKOS : 거울이 나의 삶의 낙이야")
            }
            _easterEgg++
        }

        srMyPage.setOnRefreshListener {
            mypageSharedViewModel.triggerRefresh()
            srMyPage.isRefreshing = false
        }
    }

    private fun observeUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.userInfo.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect { userInfo ->
                if (userInfo != null) {
                    binding.apply {
                        val file =
                            FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")

                        file.downloadUrl.addOnSuccessListener { url ->
                            displayImage(url.toString())
                        }.addOnFailureListener { _ ->
                            Log.e("Image Load Error", "이미지 Url 가져오는데 실패")
                        }
                        pbMyPage.visibility = View.GONE
                        tvUserName.text = userInfo.nickname
                        tvUserIntroduce.text = userInfo.introduce
                        tvUserPostNum.text = userInfo.feedCount.toFormatShortenedString()
                        tvUserCommentNum.text = userInfo.commentCount.toFormatShortenedString()
                        mbMyPageAdmin.visibility = if (userInfo.isAdmin) View.VISIBLE else View.GONE
                    }
                } else {
                    binding.pbMyPage.visibility = View.VISIBLE
                    Log.e("MyPageFragment", "UserInfo is null")
                }
            }
        }
    }

    private fun displayImage(imageUrl: String?) {
        if (isAdded) {
            if (imageUrl != null) {
                Glide.with(this).load(imageUrl).preload()
                Glide.with(this).load(imageUrl).into(binding.ivProfileImage)
                Glide.with(this).load(imageUrl).onlyRetrieveFromCache(true)
                    .into(binding.ivProfileImage)
                Glide.with(this).load(imageUrl).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivProfileImage)
            } else {
                Glide.with(this)
                    .load(R.drawable.baseline_person_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivProfileImage)
            }
        }
    }

    private fun refreshUserInfo() {
        uid?.let {
            userViewModel.getUserInfo(it) // 최신 데이터를 불러오기만 함
        } ?: run {
            Log.e("userUid", "uid 조회 실패")
        }
    }

    companion object {
        var _easterEgg = 0
        val easterEgg get() = _easterEgg
    }

    fun toast(bread: String) {
        Toast.makeText(requireContext(), bread, Toast.LENGTH_SHORT).show()
    }
}