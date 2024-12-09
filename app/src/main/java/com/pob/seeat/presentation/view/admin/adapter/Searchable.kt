package com.pob.seeat.presentation.view.admin.adapter

import com.pob.seeat.presentation.view.admin.items.AdminSearchTypeEnum

interface Searchable {
    fun performSearch(type: AdminSearchTypeEnum, query: CharSequence?)
}