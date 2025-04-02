package com.example.gomitasmv.presentation.result
import com.example.gomitasmv.domain.model.GomitasClassification
import java.io.File

 // Estado de la UI para la pantalla de resultados

data class ResultState(
    val isLoading: Boolean = false,
    val selectedImageFile: File? = null,
    val enhanceImage: Boolean = false,
    val classification: GomitasClassification? = null,
    val errorMessage: String? = null
)