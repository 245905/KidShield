package com.dominik.control.kidshield.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dominik.control.kidshield.data.model.Resource
import com.dominik.control.kidshield.data.model.dto.LoginRequest
import com.dominik.control.kidshield.data.model.dto.RegisterRequest
import com.dominik.control.kidshield.data.model.dto.UserType
import com.dominik.control.kidshield.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
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

data class LoginUiState(
    val registerScreen: Boolean = false,
    val username: String = "",
    val password: String = "",
    val userType: UserType = UserType.MONITORED,
    val loading: Boolean = false,
)

sealed class LoginEvent {
    data object NavigateToHome : LoginEvent()
    data class ShowMessage(val msg: String) : LoginEvent()
}

@HiltViewModel
class LoginViewModel@Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()  {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun updateLogin(username: String){
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String){
        _uiState.update { it.copy(password = password) }
    }

    fun updateUserType(userType: UserType){
        _uiState.update { it.copy(userType = userType) }
    }

    fun toggleScreen(){
        _uiState.update { it.copy(registerScreen = !uiState.value.registerScreen) }
    }

    fun login(){
        val username = uiState.value.username
        val password = uiState.value.password
        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch { _events.emit(LoginEvent.ShowMessage("Username and password required")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            when (val res = authRepository.login(LoginRequest(username, password))) {
                is Resource.Success -> {
                    _uiState.update { it.copy(loading = false) }
                    _events.emit(LoginEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    val msg = when (val t = res.throwable) {
                        is HttpException -> "Server error: ${t.code()}"
                        is IOException -> "Network error"
                        else -> t.localizedMessage ?: "Unknown error"
                    }
                    _uiState.update { it.copy(loading = false) }
                    _events.emit(LoginEvent.ShowMessage(msg))
                }
                Resource.Loading -> {  }
            }
        }
    }

    fun register(){
        val username = uiState.value.username
        val password = uiState.value.password
        val userType = uiState.value.userType
        if (username.isBlank() || password.isBlank()) {
            viewModelScope.launch { _events.emit(LoginEvent.ShowMessage("Username and password required")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            when (val res = authRepository.register(RegisterRequest(username, password, userType))) {
                is Resource.Success -> {
                    _uiState.update { it.copy(loading = false) }
                    login()
                }
                is Resource.Error -> {
                    val msg = when (val t = res.throwable) {
                        is HttpException -> "Server error: ${t.code()}"
                        is IOException -> "Network error"
                        else -> t.localizedMessage ?: "Unknown error"
                    }
                    _uiState.update { it.copy(loading = false) }
                    _events.emit(LoginEvent.ShowMessage(msg))
                }
                Resource.Loading -> {  }
            }
        }
    }

}
