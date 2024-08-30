package com.pob.seeat.presentation.view.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.pob.seeat.BuildConfig
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentMyPageBinding
import com.pob.seeat.presentation.view.mypage.settings.SettingsActivity
import com.pob.seeat.utils.GoogleAuthUtil

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
        mbMyPageTerms.setOnClickListener {
            findNavController().navigate(R.id.action_my_page_to_terms_of_service)
        }
        mbMyPageOss.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.my_page_oss_license))
        }
        tvMyPageVersion.text = getString(R.string.my_page_version, BuildConfig.VERSION_NAME)

        mbMyPageSettings.setOnClickListener {
            startActivity(Intent(requireContext(),SettingsActivity::class.java))
        }

        mbMyPageLogout.setOnClickListener {
            GoogleAuthUtil.googleLogout(requireActivity())
        }

        tvMyPageVersion.setOnClickListener {
            when((easterEgg) % 3) {
                0 -> toast("IDKOS : 난 너무 잘생겼어")
                1 -> toast("IDKOS : 날 보면 볼수록 너무 좋아")
                2 -> toast("IDKOS : 거울이 나의 삶의 낙이야")
            }
            _easterEgg++
        }
    }

    companion object {
        var _easterEgg = 0
        val easterEgg get() = _easterEgg
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun toast(bread: String) {
        Toast.makeText(requireContext(), bread, Toast.LENGTH_SHORT).show()
    }
}