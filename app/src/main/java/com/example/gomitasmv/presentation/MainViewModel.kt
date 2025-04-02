package com.example.gomitasmv.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomitasmv.data.model.ApiResult
import com.example.gomitasmv.data.repositorie.ClassifierRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = ClassifierRepository()

    // Estado de la UI
    private val _uiState = MutableLiveData(MainState())
    val uiState: LiveData<MainState> = _uiState

     // Comprueba si la API está activa

    fun checkApiHealth() {
        // Actualizar estado a cargando
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            apiStatusText = "Estado de la API: Verificando..."
        )

        viewModelScope.launch {
            when (val result = repository.checkApiHealth()) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        isApiConnected = true,
                        apiStatusText = "API conectada: ${result.data.status}"
                    )
                    // Si la API está activa, cargar las clases disponibles
                    getAvailableClasses()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        isApiConnected = false,
                        apiStatusText = "Error de conexión a la API",
                        errorMessage = result.errorMessage
                    )
                }
                is ApiResult.Loading -> {
                    // No hacer nada, ya actualizamos el estado
                }
            }
        }
    }
     // Obtiene las clases disponibles para clasificación

    private fun getAvailableClasses() {
        viewModelScope.launch {
            when (val result = repository.getAvailableClasses()) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value?.copy(
                        availableClasses = result.data.classes,
                        availableClassesText = "Clases disponibles: ${result.data.classes.joinToString(", ")}"
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        availableClassesText = "No se pudieron cargar las clases disponibles",
                        errorMessage = result.errorMessage
                    )
                }
                is ApiResult.Loading -> {
                    // No hacer nada, ya mostramos cargando
                }
            }
        }
    }
}