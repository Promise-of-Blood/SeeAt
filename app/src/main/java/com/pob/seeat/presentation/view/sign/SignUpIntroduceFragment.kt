package com.pob.seeat.presentation.view.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pob.seeat.MainActivity
import com.pob.seeat.databinding.FragmentSignUpIntroduceBinding
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.NotificationTokenUtils.getNotificationToken
import com.pob.seeat.utils.dialog.Dialog.showProfileConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpIntroduceFragment : Fragment() {

    private var _binding: FragmentSignUpIntroduceBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserInfoViewModel by activityViewModels()

    private val storage = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpIntroduceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() = with(binding) {
        tvSkip.setOnClickListener {
            showProfileConfirmDialog(
                requireContext(),
                onConfirm = {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            )
        }

        btnSignupNext.setOnClickListener {
            //클릭했을때 홈화면으로 넘어가게 + 한줄소개 저장
            val introduce = etvSignupIntroduce.text.toString()

            if (introduce.isNotBlank()) {
                userViewModel.saveTempUserInfo(introduce = introduce)
                Log.d("TempUserInfo", "tempUserInfo : ${userViewModel.tempUserInfo.value}")

                val uid = userViewModel.tempUserInfo.value?.uid ?: ""
                val name = userViewModel.tempUserInfo.value?.name ?: ""
                val email = userViewModel.tempUserInfo.value?.email ?: ""
                val profile = userViewModel.tempUserInfo.value?.profileUrl ?: ""
                val introduce = userViewModel.tempUserInfo.value?.introduce ?: ""
                val token = getNotificationToken()
                Log.d("토큰", "토큰이지롱  : $token")

                userViewModel.signUp(uid, email, name, profile, introduce, token)

                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            } else {
                Toast.makeText(requireContext(), "한줄소개를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }


}