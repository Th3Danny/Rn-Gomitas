package com.example.gomitasmv.presentation.result


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomitasmv.data.model.ApiResult
import com.example.gomitasmv.domain.usecase.ClassifyGomitasUseCase
import kotlinx.coroutines.launch
import java.io.File


class ResultViewModel : ViewModel() {

    private val classifyButtonUseCase = ClassifyGomitasUseCase()

    // Estado de la UI
    private val _uiState = MutableLiveData(ResultState())
    val uiState: LiveData<ResultState> = _uiState

     // Establece el archivo de imagen seleccionado

    fun setSelectedImageFile(file: File?) {
        _uiState.value = _uiState.value?.copy(selectedImageFile = file)
        file?.let {
            predictImage(_uiState.value?.enhanceImage ?: false)
        }
    }

     // Establece si se debe mejorar la imagen

    fun setEnhanceImage(enhance: Boolean) {
        _uiState.value = _uiState.value?.copy(enhanceImage = enhance)

        // Si ya tenemos un archivo de imagen, volver a analizar
        _uiState.value?.selectedImageFile?.let {
            predictImage(enhance)
        }
    }


     // Envía la imagen para clasificación

    fun predictImage(enhance: Boolean = false) {
        val imageFile = _uiState.value?.selectedImageFile ?: return

        // Actualizar estado a cargando
        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            when (val result = classifyButtonUseCase.execute(imageFile, enhance)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        classification = result.data,
                        errorMessage = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = result.errorMessage
                    )
                }
                is ApiResult.Loading -> {
                    // No hacer nada, ya actualizamos el estado
                }
            }
        }
    }
     // Limpia los resultados

    fun clearResults() {
        _uiState.value = ResultState()
    }
}