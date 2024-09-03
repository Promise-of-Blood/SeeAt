package com.pob.seeat.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.parcelize.RawValue

//@Parcelize
data class CommentModel(
    val commentId: String = "",
    val user: @RawValue DocumentReference? = null,
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
    val userImage: String = "",
    val userNickname: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        commentId = parcel.readString() ?: "",
        // TODO parcelize 방식 개선
        user = parcel.readString()?.let {
            FirebaseFirestore.getInstance().collection("user").document(it.substringAfterLast("/"))
        },
        comment = parcel.readString() ?: "",
        likeCount = parcel.readInt(),
        timeStamp = parcel.readParcelable(Timestamp::class.java.classLoader),
        userImage = parcel.readString() ?: "",
        userNickname = parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commentId)
        parcel.writeString(user?.path)
        parcel.writeString(comment)
        parcel.writeInt(likeCount)
        parcel.writeParcelable(timeStamp, flags)
        parcel.writeString(userImage)
        parcel.writeString(userNickname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommentModel> {
        override fun createFromParcel(parcel: Parcel): CommentModel {
            return CommentModel(parcel)
        }

        override fun newArray(size: Int): Array<CommentModel?> {
            return arrayOfNulls(size)
        }
    }
}