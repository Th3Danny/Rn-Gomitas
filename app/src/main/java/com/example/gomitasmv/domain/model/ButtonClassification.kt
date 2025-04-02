package com.example.gomitasmv.domain.model

import com.example.gomitasmv.data.model.ButtonStatus
import java.io.Serializable

data class ButtonClassification(
    val status: ButtonStatus,
    val confidence: Float,
    val allProbabilities: Map<String, Float>,
    val processingTimeSeconds: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable