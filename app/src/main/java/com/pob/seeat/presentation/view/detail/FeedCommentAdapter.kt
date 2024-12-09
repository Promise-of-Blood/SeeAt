package com.pob.seeat.presentation.view.detail

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.R
import com.pob.seeat.databinding.ItemCommentBinding
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.presentation.view.common.ViewHolder
import com.pob.seeat.presentation.viewmodel.CommentViewModel
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime

class FeedCommentAdapter(
    private val viewModel: CommentViewModel,
    private val onClick: (CommentModel) -> Unit,
    private val onLongClick: (CommentModel) -> Unit
) : ListAdapter<CommentModel, FeedCommentAdapter.PostViewHolder>(object :
    DiffUtil.ItemCallback<CommentModel>() {
    override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
        return oldItem.commentId == newItem.commentId
    }

    override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
        return oldItem == newItem
    }
}) {
    private var isAdmin = false

    // ViewHolder 정의
    class PostViewHolder(
        private val binding: ItemCommentBinding,
        private val viewModel: CommentViewModel,
        private val onClick: (CommentModel) -> Unit,
        private val onLongClick: (CommentModel) -> Unit,
        private val isAdmin: Boolean = false,
    ) : ViewHolder<CommentModel>(binding.root) {

        override fun onBind(item: CommentModel) = with(binding) {

            val uid = item.user?.id.toString()
            val feedId = item.feedId

            viewModel.checkMyComment(uid)
            isOwnerComment(tvCommentFeedOner, uid, feedId)
            // UI 요소 초기화

            val userRef = item.user

            userRef?.get()?.addOnSuccessListener { snapshot ->
                if (snapshot != null && snapshot.exists()) {
                    val userImageUrl = snapshot.getString("profileUrl")

                    if (!userImageUrl.isNullOrEmpty()) {
                        Glide.with(itemView.context)
                            .load(userImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(ivCommentItemUserImage)
                    } else {
                        ivCommentItemUserImage.setImageResource(R.drawable.baseline_person_24)
                    }
                }
            }



            tvCommentItemUsername.text = item.userNickname
            tvCommentItemTimeStamp.text = item.timeStamp?.toLocalDateTime()?.toKoreanDiffString()
            tvCommentItemContent.text = item.comment


            // UI 업데이트: 사용자의 댓글 여부와 소유자/관리자 여부에 따른 배경 및 표시 설정

            if (isAdmin) {
                if (item.commentId == viewModel.highlightComment.value) {
                    clCommentLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.admin_red
                        )
                    )
                    startBlinkAnimation()
                } else {
                    clCommentLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.white
                        )
                    )
                    stopBlinkAnimation()
                }
            } else if (viewModel.isMyComment.value) {
                clCommentLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.background_gray
                    )
                )
            } else {
                clCommentLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.white
                    )
                )
            }

            if (viewModel.isOwnerComment.value[uid] ?: false) {
                tvCommentFeedOner.visibility = View.VISIBLE
            } else {
                tvCommentFeedOner.visibility = View.GONE
            }

            // 클릭 리스너 설정
            clCommentLayout.setOnClickListener { if (!isAdmin) onClick(item) }
            clCommentLayout.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }

        private val animator = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f, 1f)

        private fun startBlinkAnimation() {
            animator.duration = 500
            animator.repeatCount = 1
            animator.repeatMode = ValueAnimator.REVERSE
            animator.start()
        }

        private fun stopBlinkAnimation() {
            animator?.cancel()
            itemView.alpha = 1f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, viewModel, onClick, onLongClick, isAdmin)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item) // `onBind` 메서드 호출
    }

    fun getHighlightCommentPosition(commentId: String): Int {
        return currentList.indexOfFirst { it.commentId == commentId }
    }

    fun setIsAdmin(isAdmin: Boolean) {
        this.isAdmin = isAdmin
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
