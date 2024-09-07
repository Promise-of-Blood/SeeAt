package com.pob.seeat.presentation.view.mypage

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentTermsOfServiceBinding

class TermsOfServiceFragment : Fragment() {
    private var _binding: FragmentTermsOfServiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsOfServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
        _binding = null
    }

    private fun initView() = with(binding) {
        (activity as MainActivity).setBottomNavigationVisibility(View.GONE)
        val inputStream = resources.openRawResource(R.raw.terms_of_service)
        val terms = inputStream.bufferedReader().use { it.readText() }
        tvTermsOfService.text = Html.fromHtml(terms, Html.FROM_HTML_MODE_COMPACT)
        tbTermsOfService.apply {
            setNavigationIcon(R.drawable.ic_arrow_left)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}