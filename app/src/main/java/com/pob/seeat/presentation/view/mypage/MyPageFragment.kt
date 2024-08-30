package com.pob.seeat.presentation.view.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.pob.seeat.BuildConfig
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentMyPageBinding
import com.pob.seeat.presentation.view.mypage.settings.SettingsActivity
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {


        val uid = getUserUid()
        if (uid != null) {
            userViewModel.getUserInfo(uid)
            observeUserInfo()
        } else {
            Log.e("userUid", "uid 조회 실패")
        }


        val profileUri = userViewModel.userInfo.value?.profileUrl?.toUri()
        ivProfileImage.setImageURI(profileUri)

        val userName = userViewModel.userInfo.value?.nickname
        tvUserName.text = userName


        tvUserPostNum

        tvUserCommentNum

        tvUserIntroduce.text = userViewModel.userInfo.value?.introduce

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
    }

    private fun observeUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.userInfo.collect() { userInfo ->
                if (userInfo != null) {
                    binding.apply {
                        ivProfileImage.setImageURI(userInfo.profileUrl.toUri())
                        tvUserName.text = userInfo.nickname
                        tvUserIntroduce.text = userInfo.introduce
                    }
                } else {
                    Log.e("MyPageFragment", "UserInfo is null")
                }
            }

        }
    }
}


