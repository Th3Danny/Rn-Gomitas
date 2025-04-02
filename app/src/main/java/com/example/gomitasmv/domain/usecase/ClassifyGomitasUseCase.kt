package com.example.gomitasmv.domain.usecase

import com.example.gomitasmv.data.model.ApiResult
import com.example.gomitasmv.data.model.GomitasStatus
import com.example.gomitasmv.data.repositorie.ClassifierRepository
import com.example.gomitasmv.domain.model.GomitasClassification
import java.io.File


class ClassifyGomitasUseCase(
    private val repository: ClassifierRepository = ClassifierRepository()
) {
     // Clasifica un botón a partir de una imagen

    suspend fun execute(imageFile: File, enhance: Boolean = false): ApiResult<GomitasClassification> {
        return when (val result = repository.predictImage(imageFile, enhance)) {
            is ApiResult.Success -> {
                val data = result.data
                val buttonStatus = GomitasStatus.fromString(data.prediction)

                // Convertir all_predictions a un Map para mantener la compatibilidad
                val probabilitiesMap = data.all_predictions.associate {
                    it.className to it.confidence
                }

                ApiResult.Success(
                    GomitasClassification(
                        status = buttonStatus,
                        confidence = data.confidence,
                        allProbabilities = probabilitiesMap,
                        processingTimeSeconds = data.processing_time
                    )
                )
            }
            is ApiResult.Error -> ApiResult.Error(result.errorMessage, result.code)
            is ApiResult.Loading -> ApiResult.Loading
        }
    }
}