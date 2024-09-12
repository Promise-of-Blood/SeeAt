package com.pob.seeat.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.naver.maps.geometry.LatLng
import com.pob.seeat.domain.model.TagModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class NewFeedViewModel : ViewModel() {
    // MutableStateFlow를 사용하여 상태 관리
    private val _selectTagList = MutableStateFlow<List<TagModel>>(emptyList())
    val selectTagList: StateFlow<List<TagModel>> = _selectTagList.asStateFlow()

    private val _feedImageUploadResult = MutableStateFlow<String?>(null)
    val feedImageUploadResult: StateFlow<String?> = _feedImageUploadResult

    var feedImageList: List<String> = emptyList()
    private val storage = FirebaseStorage.getInstance().reference

    var selectLocation: LatLng? = null


    fun updateSelectTagList(newList: List<TagModel>) {
        viewModelScope.launch {
            _selectTagList.value = newList
        }
        Timber.tag("NewFeedViewModel").d("updateSelectTagList: $newList")
    }

    fun updateSelectLocation(newLocation: LatLng) {
        viewModelScope.launch {
            selectLocation = newLocation
        }
        Timber.tag("NewFeedViewModel").d("updateSelectLocation: $newLocation")

    }

    fun uploadFeedImageList(imageUriList: List<Uri>, feedId: String) {
        viewModelScope.launch {
            feedImageList = emptyList()
            flow {
                val downloadUrlList = withContext(Dispatchers.IO) {
                    imageUriList.mapIndexed { index, imageUri ->
                        val file = storage.child("feed_images/$feedId/${index}.jpg")
                        file.putFile(imageUri).await()
                        file.downloadUrl.await().toString()
                    }
                }
                emit(downloadUrlList) // 업로드한 이미지의 다운로드 URL 리스트 emit
            }
                .onStart {
                    // 업로드 시작할 때 필요한 로직 추가
                    Log.d("FeedImageUpload", "이미지 업로드 시작")
                    _feedImageUploadResult.value = "LOADING"
                }
                .catch { e ->
                    // 에러 처리
                    Log.e("FeedImageUpload", "이미지 업로드 실패 ${e.message}")
                    emit(emptyList<String>()) // 실패 시 빈 리스트 반환
                }
                .collect { downloadUrlList ->
                    // 업로드 성공 시 결과 처리
                    feedImageList = downloadUrlList
                    _feedImageUploadResult.value = "SUCCESS"

                    Log.d("FeedImageUpload", "$downloadUrlList")
                }
        }
    }

    fun uploadFeed(feedData: HashMap<String, Any>, feedId: String) {
        viewModelScope.launch {
            flow {
                // Firestore에 피드 데이터 저장
                val firestore = FirebaseFirestore.getInstance()
                val feedDocument = firestore.collection("feed").document(feedId)
                feedDocument.set(feedData).await()
                emit("SUCCESS")
            }
                .onStart {
                    Log.d("NewFeedUpload", "피드 업로드 시작")
                }
                .catch { e ->
                    Log.e("NewFeedUpload", "피드 업로드 실패 ${e.message}")
                    emit("ERROR")
                }
                .collect {
                    Log.d("NewFeedUpload", "피드 업로드 완료")
                }
        }
    }


}