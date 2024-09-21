package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.usecase.GetUserListUseCase
import com.pob.seeat.presentation.view.admin.items.AdminListItem
import com.pob.seeat.presentation.view.admin.items.toUserAdminListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminUserViewModel @Inject constructor(
    private val getUserListUseCase: GetUserListUseCase,
) : ViewModel() {
    var userList = MutableStateFlow<Result<List<AdminListItem>>>(Result.Loading)
        private set

    fun getUserList() {
        viewModelScope.launch {
            getUserListUseCase().collect {
                userList.value = when (it) {
                    is Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(it.data.toUserAdminListItem())
                    is Result.Error -> Result.Error(it.message)
                }
            }
        }
    }

    init {
        getUserList()
    }
}