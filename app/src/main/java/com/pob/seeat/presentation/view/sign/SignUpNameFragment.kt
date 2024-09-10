package com.pob.seeat.presentation.view.sign

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.pob.seeat.databinding.FragmentSignUpNameBinding
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpNameFragment : Fragment() {

    private var _binding: FragmentSignUpNameBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpNameBinding.inflate(inflater, container, false)
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

        val name = userViewModel.tempUserInfo.value?.name ?:"활동명을 입력해주세요"
        etvSignupName.setText(name)

        btnSignupNext.setOnClickListener {

            if (name.isNotBlank()) {

                val etvText = etvSignupName.text.toString()

                userViewModel.saveTempUserInfo(name = etvText)
                Log.d("TempUserInfo","tempUserInfo : ${userViewModel.tempUserInfo.value}")

                (activity as? SignUpActivity)?.let { activity ->
                    activity.signUpBinding.vpSignUp.currentItem += 1
                }
            } else {
                Toast.makeText(requireContext(), "활동명을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }


    }


}

