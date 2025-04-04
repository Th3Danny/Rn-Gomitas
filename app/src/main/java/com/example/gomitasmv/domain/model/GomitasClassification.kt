package com.example.gomitasmv.domain.model

import com.example.gomitasmv.data.model.GomitasStatus
import java.io.Serializable

data class GomitasClassification(
    val status: GomitasStatus,
    val confidence: Float,
    val allProbabilities: Map<String, Float>,
    val processingTimeSeconds: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable