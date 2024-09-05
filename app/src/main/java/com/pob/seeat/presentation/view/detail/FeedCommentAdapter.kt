package com.pob.seeat.presentation.view.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.R
import com.pob.seeat.databinding.ItemCommentBinding
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.presentation.view.common.ViewHolder
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime

class FeedCommentAdapter(private val onClick: (CommentModel) -> Unit) :
    ListAdapter<CommentModel, ViewHolder<CommentModel>>(object :
        DiffUtil.ItemCallback<CommentModel>() {
        override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }


    }) {
    class PostViewHolder(
        val binding: ItemCommentBinding,
        private val onClick: (CommentModel) -> Unit
    ) :
        ViewHolder<CommentModel>(binding.root) {
        override fun onBind(item: CommentModel) = with(binding) {
            Glide.with(itemView.context)
                .load(item.userImage)
                .into(ivCommentItemUserImage)
            tvCommentItemUsername.text = item.userNickname
            tvCommentItemTimeStamp.text = item.timeStamp?.toLocalDateTime()?.toKoreanDiffString()
            tvCommentItemContent.text = item.comment

            val userId = item.user?.id.toString()
            val feedId = item.feedId

            isMyComment(clCommentLayout, userId)
            isOwnerComment(tvCommentFeedOner, userId, feedId)

            clCommentLayout.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<CommentModel> {
        return PostViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<CommentModel>, position: Int) {
        holder.onBind(getItem(position))
    }

}

fun isMyComment(layout: ConstraintLayout, commentUserUid: String) {
    layout.apply {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid

        if (userUid == commentUserUid) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.background_gray))
        } else {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}


fun isOwnerComment(textView: TextView, commentUserUid: String, feedId: String) {
    val firestore = FirebaseFirestore.getInstance()

    // 'feed' 컬렉션의 특정 문서를 가져오기
    val feedDocRef = firestore.collection("feed").document(feedId)

    feedDocRef.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // 'user' 필드에서 DocumentReference 가져오기
                val userReference = documentSnapshot.get("user")

                when (userReference) {
                    is DocumentReference -> {
                        Log.d("Firestore", "DocumentReference: ${userReference.path}")
                        userReference.get()
                            .addOnSuccessListener { userSnapshot ->
                                if (userSnapshot.exists()) {
                                    // uid 필드를 가져오기
                                    val ownerUid = userSnapshot.getString("uid")
                                    Log.d("Firestore", "Fetched owner UID: $ownerUid")
                                    if (commentUserUid == ownerUid) {
                                        textView.visibility = View.VISIBLE
                                    } else {
                                        textView.visibility = View.GONE
                                    }
                                } else {
                                    Log.e("Firestore", "유저 문서를 찾을 수 없습니다: ${userReference.path}")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "유저 문서 가져오기 실패: ${exception.message}")
                            }
                    }

                    is String -> {
                        // user 필드가 단순히 UID를 나타내는 String인 경우
                        val ownerUid = userReference
                        Log.d("Firestore", "Fetched owner UID from String: $ownerUid")

                    }

                    else -> {
                        // user 필드가 기대하지 않은 타입인 경우 처리
                        Log.e("Firestore", "'user' 필드가 예상한 타입이 아닙니다: $userReference")
                    }
                }
            } else {
                Log.e("Firestore", "게시물을 찾을 수 없습니다: feedId = $feedId")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "데이터 가져오기 실패: ${exception.message}")
        }
}