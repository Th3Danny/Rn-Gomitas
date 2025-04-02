package com.example.gomitasmv.data.model

import com.google.gson.annotations.SerializedName


data class ClassifierResponse(
    val prediction: String,
    val confidence: Float,
    val all_predictions: List<PredictionItem>,
    val status: String,
    val processing_time: Float? = null
)

data class PredictionItem(
    @field:SerializedName("class") val className: String,
    val confidence: Float
)
 //Clase para respuestas de API genéricas

data class ClassesResponse(
    val classes: List<String>
)


  //Clase para respuesta de salud de la API

data class HealthResponse(
    val status: String,
    val timestamp: Long
)


 // Clase para encapsular los resultados de las llamadas a la API

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val errorMessage: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()

    // Funciones de extensión para facilitar el manejo de los resultados
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    // Obtener datos o null si no es Success
    fun getOrNull(): T? = if (this is Success) data else null
}