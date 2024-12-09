package com.pob.seeat.data.remote

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.pob.seeat.data.model.chat.ChatFeedInfoModel
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FeedRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) : GetFeedList, DetailFeed {

    override suspend fun getFeedList(
        centerLat: Double,
        centerLng: Double,
        userLocation: GeoPoint,
        radiusInKm: Double,
        sortBy: String, // date, distance, like
    ): List<FeedModel> {
        val center = GeoLocation(centerLat, centerLng)
        val radiusInM = radiusInKm * 1000 // 반경을 미터로 변환

        // GeoHash 쿼리로 GeoPoint 필드를 기준으로 필터링
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks = mutableListOf<Task<QuerySnapshot>>()

        // feed 컬렉션의 geoHash를 사용하여 각 범위에 대해 쿼리 생성 및 실행
        for (b in bounds) {
            val query = firestore.collection("feed").orderBy("geohash") // GeoHash 필드를 기준으로 정렬
                .apply {
                    when (sortBy) {
                        "date" -> orderBy("date")
                        "like" -> orderBy("like")
                    }
                }.startAt(b.startHash).endAt(b.endHash)

            Timber.d("Query: $query")
            tasks.add(query.get())
        }

        Timber.d("Tasks: $tasks")

        // 모든 쿼리 결과를 비동기적으로 기다림
        val results = Tasks.whenAllComplete(tasks).await()
        val matchingFeeds = mutableListOf<FeedModel>()

        for (task in tasks) {
            val querySnapshot = task.result
            if (querySnapshot != null) {
                for (documentSnapshot in querySnapshot.documents) {
                    val lat = documentSnapshot.getGeoPoint("location")?.latitude
                    val lng = documentSnapshot.getGeoPoint("location")?.longitude

                    Timber.d("documentSnapshot: $documentSnapshot")

                    // 위치 정보가 있는 경우만 필터링
                    if (lat != null && lng != null) {
                        val docLocation = GeoLocation(lat, lng)

                        // 지도 중심점과의 거리
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)

                        // 실제 사용자 위치와의 거리
                        val userDistance = GeoFireUtils.getDistanceBetween(
                            docLocation, GeoLocation(userLocation.latitude, userLocation.longitude)
                        )

                        // 반경 내에 있는 문서만 처리
                        if (distanceInM <= radiusInM) {
                            val tagList = documentSnapshot.get("tagList") as? List<*>

                            Timber.tag("distance").d("distance: $distanceInM")
                            Timber.tag("distance").d("distanceInt: ${distanceInM.toInt()}")

                            val feedModel = documentSnapshot.toObject(FeedModel::class.java)?.copy(
                                feedId = documentSnapshot.id,
                                tags = tagList?.filterIsInstance<String>() ?: emptyList(),
                                distance = userDistance.toInt()
                            )?.run {
                                val nickname = (user as? DocumentReference)?.get()?.await()
                                    ?.getString("nickname") ?: "탈퇴한 사용자"

                                nickname?.let {
                                    copy(nickname = it)
                                } ?: this
                            }

                            // 유효한 feedModel만 리스트에 추가
                            feedModel?.let { matchingFeeds.add(it) }
                        }
                    }
                }
            }
        }

        Log.d("FeedRemote", "Filtered feeds: $matchingFeeds")

        when (sortBy) {
            "date" -> matchingFeeds.sortByDescending { it.date }
            "like" -> matchingFeeds.sortByDescending { it.like }
            "distance" -> matchingFeeds.sortBy { it.distance }
        }

        return matchingFeeds
    }

    override suspend fun getFeedById(postId: String, userLocation: GeoPoint): FeedModel? {
        val documentSnapshot = firestore.collection("feed").document(postId).get().await()
        val commentsSnapshot =
            firestore.collection("feed").document(postId).collection("comments").get().await()
        val comments =
            commentsSnapshot.documents.mapNotNull { it.toObject(CommentModel::class.java) }

        val tagList = documentSnapshot.get("tagList") as? List<*>
        val userRef = documentSnapshot.getDocumentReference("user")

        Timber.tag("distance").d("userLocation FeedRemote: $userLocation")

        return documentSnapshot.toObject(FeedModel::class.java)?.copy(
            user = userRef,
            feedId = documentSnapshot.id,
            tags = tagList?.filterIsInstance<String>() ?: emptyList(),
            comments = comments,
        )?.run {
            val userDocument = (user as? DocumentReference)?.get()?.await()
            val userData = userDocument?.data
            val nickname = userData?.get("nickname") as? String ?: "탈퇴한 사용자"
            val userImage = userData?.get("profileUrl") as? String
                ?: "https://firebasestorage.googleapis.com/v0/b/see-at.appspot.com/o/profile_images%2Fiv_main_icon.png?alt=media&token=33eb6196-76b4-419d-8bc3-f986219b290b"
            val lat = documentSnapshot.getGeoPoint("location")?.latitude ?: 0.0
            val lng = documentSnapshot.getGeoPoint("location")?.longitude ?: 0.0
            val userDistance = if (userLocation == GeoPoint(0.0, 0.0) || lat == 0.0 || lng == 0.0) {
                0.0
            } else {
                GeoFireUtils.getDistanceBetween(
                    GeoLocation(lat, lng),
                    GeoLocation(userLocation.latitude, userLocation.longitude)
                )
            }
            // 로그로 nickname 값을 출력하여 확인
            Log.d("FeedRemote", "Fetched nickname: $nickname for user: ${user?.id}")
            copy(
                nickname = nickname.toString(),
                userImage = userImage.toString(),
                distance = userDistance.toInt()
            )
        }
    }

    suspend fun getUserByFeedId(feedId: String): ChatFeedInfoModel {
        val feedUserRef =
            firestore.collection("feed").document(feedId).get().await().getDocumentReference("user")
        return ChatFeedInfoModel(
            nickname = feedUserRef?.get()?.await()?.getString("nickname") ?: "(알 수 없음)",
            profileUrl = feedUserRef?.get()?.await()?.getString("profileUrl"),
        )
    }

    suspend fun getFeedListById(feedIdList: List<String>): List<FeedModel> {
        val feedCollection = firestore.collection("feed")
        val feedDocuments = mutableListOf<DocumentSnapshot>()

        // ID 리스트를 10개씩 나눠서 여러 쿼리 실행 (whereIn은 한 번에 10개씩 가져올 수 있음)
        feedIdList.chunked(10).forEach { idsChunk ->
            val querySnapshot =
                feedCollection.whereIn(FieldPath.documentId(), idsChunk).get().await()
            feedDocuments.addAll(querySnapshot.documents)
        }
        return feedDocuments.mapNotNull { documentSnapshot ->
            val userDocument = documentSnapshot.getDocumentReference("user")?.get()?.await()
            val tagList = documentSnapshot.get("tagList") as? List<*>
            val imageList = documentSnapshot.get("contentImage") as? List<*>
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                nickname = userDocument?.getString("nickname") ?: "탈퇴한 사용자",
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList(),
                contentImage = imageList?.filterIsInstance<String>() ?: emptyList(),
            )
        }
    }

    override suspend fun updateLikePlus(postId: String) {
        firestore.collection("feed").document(postId).update("like", FieldValue.increment(1))
            .await()
    }

    override suspend fun updateLikeMinus(postId: String) {
        firestore.collection("feed").document(postId).update("like", FieldValue.increment(-1))
            .await()
    }

    override suspend fun removeFeed(postId: String) {
        firestore.collection("feed").document(postId).delete().await()
    }

    override suspend fun editFeed(feedModel: FeedModel) {
        val feedMap = mutableMapOf<String, Any>()
        feedMap["content"] = feedModel.content
        feedMap["contentImage"] = feedModel.contentImage
        feedMap["location"] = feedModel.location ?: GeoPoint(0.0, 0.0)
        feedMap["title"] = feedModel.title
        feedMap["tagList"] = feedModel.tags
        Timber.tag("FeedRemote").i("feedMap: $feedMap")

        firestore.collection("feed").document(feedModel.feedId).update(feedMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.i("업데이트 됨")
                } else if (it.isCanceled) {
                    Timber.i("취소됨")
                } else {
                    Timber.i("else")
                }
            }
    }


}