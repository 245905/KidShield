package com.dominik.control.kidshield.ui.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dominik.control.kidshield.data.model.domain.AppChangeType
import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.repository.AppInfoDiffRepository
import com.dominik.control.kidshield.data.repository.AppInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class DataUiState(
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

    private val _diffs = MutableLiveData<List<AppInfoDiffEntity>>()
    val diffs: LiveData<List<AppInfoDiffEntity>> = _diffs

    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DataEvent>()
    val events: SharedFlow<DataEvent> = _events.asSharedFlow()

    fun loadApps(){
//        viewModelScope.launch(Dispatchers.IO) {
//            val apps = appInfoRepository.getUserAppInfos() // suspend fun
//
//            _list.postValue(apps)
//        }
        _list.value = fakeApps
        _diffs.value = fakeDiffs
    }


}

val fakeApps = listOf(
    AppInfoEntity(
        id = 1,
        referenceNumber = 1001L,
        appName = "YouTube",
        packageName = "com.google.android.youtube",
        versionName = "18.40.12",
        versionCode = 1543210,
        firstInstallTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 300,
        lastUpdateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 2,
        isSystemApp = false,
        status = UploadStatusType.UPLOADED
    ),
    AppInfoEntity(
        id = 2,
        referenceNumber = 1002L,
        appName = "Chrome",
        packageName = "com.android.chrome",
        versionName = "118.0.5993.90",
        versionCode = 1185993090,
        firstInstallTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 500,
        lastUpdateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 1,
        isSystemApp = true,
        status = UploadStatusType.UPLOADED
    ),
    AppInfoEntity(
        id = 3,
        referenceNumber = 1003L,
        appName = "Spotify",
        packageName = "com.spotify.music",
        versionName = "8.8.82.112",
        versionCode = 80882112,
        firstInstallTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 200,
        lastUpdateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30,
        isSystemApp = false,
        status = UploadStatusType.UPLOADED
    )
)
val fakeDiffs = listOf(
    // nowa aplikacja
    AppInfoDiffEntity(
        id = 1,
        referenceNumber = 1004L,
        changeType = AppChangeType.ADDED,
        appName = "TikTok",
        packageName = "com.zhiliaoapp.musically",
        oldVersionName = "0.0.0",
        oldVersionCode = 0,
        lastUpdateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 5
    ),

    // aktualizacja
    AppInfoDiffEntity(
        id = 2,
        referenceNumber = 1001L,
        changeType = AppChangeType.UPDATED,
        appName = "YouTube",
        packageName = "com.google.android.youtube",
        oldVersionName = "18.38.45",
        oldVersionCode = 1543100,
        lastUpdateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 2
    ),

    // usunięcie
    AppInfoDiffEntity(
        id = 3,
        referenceNumber = 1005L,
        changeType = AppChangeType.REMOVED,
        appName = "Facebook",
        packageName = "com.facebook.katana",
        oldVersionName = "437.0.0.25.112",
        oldVersionCode = 4370025112,
        lastUpdateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 10
    )
)
