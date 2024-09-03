package com.pob.seeat.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.RawValue

//@Parcelize
data class FeedModel(
    val feedId: String = "",
    val user: @RawValue DocumentReference? = null,
    val nickname: String = "",
    val title: String = "",
    val content: String = "",
    val like: Int = 0,
    val commentsCount: Int = 0,
    val location: @RawValue GeoPoint? = null,
    val date: Timestamp? = null,
    val comments: List<CommentModel> = emptyList(),
    val tags: List<String> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        feedId = parcel.readString() ?: "",
        // TODO parcelize 방식 개선
        user = parcel.readString()?.let {
            FirebaseFirestore.getInstance().collection("user").document(it.substringAfterLast("/"))
        },
        nickname = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        content = parcel.readString() ?: "",
        like = parcel.readInt(),
        commentsCount = parcel.readInt(),
        location = parcel.readDouble().let { latitude ->
            val longitude = parcel.readDouble()
            if (latitude != 0.0 && longitude != 0.0) GeoPoint(latitude, longitude) else null
        },
        date = parcel.readParcelable(Timestamp::class.java.classLoader),
        comments = listOf<CommentModel>().apply {
            parcel.readList(this, CommentModel::class.java.classLoader)
        },
        tags = parcel.createStringArrayList().orEmpty(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(feedId)
        parcel.writeString(user?.path)
        parcel.writeString(nickname)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeInt(like)
        parcel.writeInt(commentsCount)
        if (location != null) {
            parcel.writeDouble(location.latitude)
            parcel.writeDouble(location.longitude)
        } else {
            parcel.writeDouble(0.0)
            parcel.writeDouble(0.0)
        }
        parcel.writeParcelable(date, flags)
        parcel.writeTypedList(comments)
        parcel.writeStringList(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedModel> {
        override fun createFromParcel(parcel: Parcel): FeedModel {
            return FeedModel(parcel)
        }

        override fun newArray(size: Int): Array<FeedModel?> {
            return arrayOfNulls(size)
        }
    }
}
