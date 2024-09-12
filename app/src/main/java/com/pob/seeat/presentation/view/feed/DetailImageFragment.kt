package com.pob.seeat.presentation.view.feed

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pob.seeat.databinding.FragmentDetailImageBinding

class DetailImageFragment : Fragment() {
    private var _binding: FragmentDetailImageBinding? = null
    private val binding get() = _binding!!

    private val args: DetailImageFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uriString = args.imageUri
        val imageUri: Uri = Uri.parse(uriString)

        binding.apply {
            ivCancel.setOnClickListener {
                findNavController().popBackStack()  // 이전 프래그먼트로 돌아감
            }

            photoView.setImageURI(imageUri)
        }
    }
}