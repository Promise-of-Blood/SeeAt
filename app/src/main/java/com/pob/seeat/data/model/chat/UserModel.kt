package com.pob.seeat.data.model.chat

data class UserModel (
    val userId : String,
    val feedIdList : List<String>,
    val chattingByFeed: Map<String, String> // 키 = 게시글, 값 = 채팅방
)