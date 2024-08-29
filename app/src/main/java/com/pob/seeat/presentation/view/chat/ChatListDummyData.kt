package com.pob.seeat.presentation.view.chat

import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

object ChatListDummyData {
    fun getDummyList() : List<UiState<ChatListUiItem>> =
        listOf(
            UiState.Success(ChatListUiItem.ChatItem(
                person = "짜글명선",
                icon = "",
                lastTime = "1분 전",
                content = "늦어서 죄송합니다. 늦어서 죄송합니다. 늦어서 죄송합니다. 늦어서 죄송합니다. 늦어서 죄송합니다.",
                unreadMessageCount = 3,
            )),
            UiState.Success(ChatListUiItem.ChatItem(
                person = "김옥춘",
                icon = "",
                lastTime = "12시간 전",
                content = "국가는 건전한 소비행위를 계도하고 생산품의 품질향상을 촉구하기 위한 소비자보호운동을 법률이 정하는 바에 의하여 보장한다. 대통령후보자가 1인일 때에는 그 득표수가 선거권자 총수의 3분의 1 이상이 아니면 대통령으로 당선될 수 없다. 형사피의자 또는 형사피고인으로서 구금되었",
                unreadMessageCount = 7,
            )),
            UiState.Success(ChatListUiItem.ChatItem(
                person = "Kangjin Lee",
                icon = "",
                lastTime = "1년 전",
                content = "거울 어딨어",
                unreadMessageCount = 1,
            )),
            UiState.Success(ChatListUiItem.ChatItem(
                person = "오너",
                icon = "",
                lastTime = "2일 전",
                content = "꽥",
                unreadMessageCount = 0,
            )),
            UiState.Success(ChatListUiItem.ChatItem(
                person = "김이무개",
                icon = "",
                lastTime = "3개월 전",
                content = "?",
                unreadMessageCount = 0,
            )),
        )
}