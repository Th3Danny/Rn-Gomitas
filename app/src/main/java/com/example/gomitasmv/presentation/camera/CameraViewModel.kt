package com.example.gomitasmv.presentation.camera

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class CameraViewModel : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableLiveData(CameraState())
    val uiState: LiveData<CameraState> = _uiState


     // Establece el archivo temporal para guardar la imagen

    fun setTempImageFile(file: File) {
        _uiState.value = _uiState.value?.copy(tempImageFile = file)
    }


     // Establece la URI de la imagen capturada

    fun setCapturedImageUri(uri: Uri?) {
        _uiState.value = _uiState.value?.copy(capturedImageUri = uri)
    }


     // Actualiza el estado de la cámara

    fun updateCameraStatus(status: CameraStatus) {
        _uiState.value = _uiState.value?.copy(
            cameraStatus = status,
            isLoading = status == CameraStatus.CAPTURING,
            errorMessage = if (status == CameraStatus.ERROR) "Error al capturar la imagen" else null
        )
    }

     // Limpia los datos de la cámara

    fun clearCameraData() {
        _uiState.value = CameraState()
    }
}