package com.dominik.control.kidshield.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominik.control.kidshield.data.model.Resource
import com.dominik.control.kidshield.data.model.dto.CheckPairStatusRequest
import com.dominik.control.kidshield.data.model.dto.PairByPinRequest
import com.dominik.control.kidshield.data.repository.PairingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

enum class PairingRole { PARENT, CHILD }

data class PairingUiState(
    val role: PairingRole = PairingRole.CHILD,
    val inputPin: String = "",
    val generatedPin: String? = null,
    val loading: Boolean = false,
    val isPaired: Boolean = false
)

sealed class PairingEvent {
    data object NavigateToHome : PairingEvent()
    data class ShowMessage(val msg: String) : PairingEvent()
}

@HiltViewModel
class PairingViewModel@Inject constructor(
    private val pairingRepository: PairingRepository
) : ViewModel()  {

    private val _uiState = MutableStateFlow(PairingUiState())
    val uiState: StateFlow<PairingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PairingEvent>()
    val events: SharedFlow<PairingEvent> = _events.asSharedFlow()

    private var pollingJob: Job? = null

    fun setRole(role: PairingRole) {
        _uiState.update { it.copy(role = role) }
    }

    fun updateInputPin(pin: String) {
        _uiState.update { it.copy(inputPin = pin) }
    }

    fun pair(){
        val pin = uiState.value.inputPin
        if (pin.length < 4) {
            viewModelScope.launch { _events.emit(PairingEvent.ShowMessage("Enter a valid PIN")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            when (val res = pairingRepository.pairByPin(PairByPinRequest(pin))) {
                is Resource.Success -> {
                    _uiState.update { it.copy(loading = false) }
                    _events.emit(PairingEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    val msg = when (val t = res.throwable) {
                        is HttpException -> "Server error: ${t.code()}"
                        is IOException -> "Network error"
                        else -> t.localizedMessage ?: "Unknown error"
                    }
                    _uiState.update { it.copy(loading = false) }
                    _events.emit(PairingEvent.ShowMessage(msg))
                }
                Resource.Loading -> {  }
            }
        }
    }

    fun generatePin(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            when (val res = pairingRepository.generateCode()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(loading = false, generatedPin = res.data.pin) }
                    startPollingStatus(res.data.pin)
                }
                is Resource.Error -> {
                    val msg = when (val t = res.throwable) {
                        is HttpException -> "Server error: ${t.code()}"
                        is IOException -> "Network error"
                        else -> t.localizedMessage ?: "Unknown error"
                    }
                    _uiState.update { it.copy(loading = false) }
                    _events.emit(PairingEvent.ShowMessage(msg))
                }
                Resource.Loading -> {  }
            }
        }
    }

    private fun startPollingStatus(pin: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3000)
                val res = pairingRepository.checkPairingStatus(CheckPairStatusRequest(pin))
                if (res is Resource.Success && res.data.isPaired) {
                    _uiState.update { it.copy(isPaired = true) }
                    _events.emit(PairingEvent.ShowMessage("Device paired successfully!"))
                    delay(1500)
                    _events.emit(PairingEvent.NavigateToHome)
                    break
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }

}
