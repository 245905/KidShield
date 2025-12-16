package com.dominik.control.kidshield.ui.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominik.control.kidshield.data.model.Resource
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.dto.LoginRequest
import com.dominik.control.kidshield.data.model.dto.RegisterRequest
import com.dominik.control.kidshield.data.model.dto.UserType
import com.dominik.control.kidshield.data.repository.AppInfoDiffRepository
import com.dominik.control.kidshield.data.repository.AppInfoRepository
import com.dominik.control.kidshield.data.repository.AuthRepository
import com.dominik.control.kidshield.data.repository.TestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class DataUiState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
)

sealed class DataEvent {
    data object NavigateToHome : DataEvent()
    data class ShowMessage(val msg: String) : DataEvent()
}

@HiltViewModel
class DataViewModel@Inject constructor(
    private val appInfoRepository: AppInfoRepository,
    private val appInfoDiffRepository: AppInfoDiffRepository
) : ViewModel()  {

    private val _list = MutableLiveData<List<AppInfoEntity>>()
    val list: LiveData<List<AppInfoEntity>> = _list

    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DataEvent>()
    val events: SharedFlow<DataEvent> = _events.asSharedFlow()

    fun loadApps(){
        viewModelScope.launch(Dispatchers.IO) {
            val apps = appInfoRepository.getUserAppInfos() // suspend fun

            _list.postValue(apps)
        }
    }


}
