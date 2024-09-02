package com.pob.seeat.presentation.view.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHomeBinding
import com.pob.seeat.databinding.FragmentNewFeedBinding
import com.pob.seeat.presentation.view.home.TagAdapter
import com.pob.seeat.utils.Utils.tagList
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import timber.log.Timber
import java.util.Date


class NewFeedFragment : Fragment() {
    private var _binding: FragmentNewFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadFeed()
    }

    private fun uploadFeed() {
        binding.apply {
            selectTag.setOnClickListener {
                val modal = NewFeedModalBottomSheet()
                modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
                modal.show(parentFragmentManager, modal.tag)
            }

            tvUpload.setOnClickListener {
                uploadFeed()
                val rand: Float = (-50..50).random().toFloat() / 1000
                Timber.tag("NewFeedRand").d("$rand")

                val latitude = 37.565381+(rand)
                val longitude = 126.97786+(rand)
                val feedData : HashMap<String, Any> = hashMapOf(
                    "title" to etTitle.text.toString(),
                    "content" to etContent.text.toString(),
                    "date" to Timestamp(Date()),
                    "tagList" to listOf("태그 1", "태그 2", "태그 3"),
                    "location" to GeoPoint(latitude, longitude),
                    "like" to 0,
                    "commentsCount" to 0,
                    "user" to "E2S91qMCU2bpUozIqxdc4LXhw3F2"
                    )
                val db = Firebase.firestore
                val ran = (0..10000).random()
                db
                    .collection("feed")
                    .document("test_feed$ran")
                    .set(feedData)
                    .addOnSuccessListener { documentReference ->
                        // 성공 시 처리
                        Timber.d("DocumentSnapshot added with ID: $documentReference")
                    }
                    .addOnFailureListener { e ->
                        // 실패 시 처리
                        Timber.w(e, "Error adding document")
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}