package com.example.gomitasmv.presentation.camera

import android.net.Uri
import java.io.File


 // Estado de la UI para la pantalla de cámara

data class CameraState(
    val isLoading: Boolean = false,
    val cameraStatus: CameraStatus = CameraStatus.IDLE,
    val capturedImageUri: Uri? = null,
    val tempImageFile: File? = null,
    val errorMessage: String? = null
)


 // Estados posibles de la cámara

enum class CameraStatus {
    IDLE,           // Cámara inactiva
    CAPTURING,      // Tomando una foto
    IMAGE_CAPTURED, // Imagen capturada
    ERROR           // Error al capturar
}