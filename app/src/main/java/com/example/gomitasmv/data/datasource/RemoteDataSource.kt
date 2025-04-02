package com.example.gomitasmv.data.datasource


import com.example.gomitasmv.core.service.ClassifierService
import com.example.gomitasmv.data.model.ApiResult
import com.example.gomitasmv.data.model.ClassifierResponse
import com.example.gomitasmv.data.model.ClassesResponse
import com.example.gomitasmv.data.model.HealthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


class RemoteDataSource {

    private val apiService = ClassifierService.apiService

    suspend fun checkApiHealth(): ApiResult<HealthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.checkHealth()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@withContext ApiResult.Success(it)
                } ?: return@withContext ApiResult.Error("Respuesta vacía del servidor")
            } else {
                return@withContext ApiResult.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: IOException) {
            return@withContext ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            return@withContext ApiResult.Error("Error inesperado: ${e.message}")
        }
    }


      //Obtiene las clases disponibles para clasificación

    suspend fun getAvailableClasses(): ApiResult<ClassesResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAvailableClasses()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@withContext ApiResult.Success(it)
                } ?: return@withContext ApiResult.Error("Respuesta vacía del servidor")
            } else {
                return@withContext ApiResult.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: IOException) {
            return@withContext ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            return@withContext ApiResult.Error("Error inesperado: ${e.message}")
        }
    }

     // Envía una imagen para clasificación

    suspend fun predictImage(imageFile: File, enhance: Boolean = false): ApiResult<ClassifierResponse> =
        withContext(Dispatchers.IO) {
            try {
                // Crear parte multipart para la imagen
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                // Llamar a la API
                val response = apiService.predictImage(body, enhance)

                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext ApiResult.Success(it)
                    } ?: return@withContext ApiResult.Error("Respuesta vacía del servidor")
                } else {
                    return@withContext ApiResult.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: IOException) {
                return@withContext ApiResult.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                return@withContext ApiResult.Error("Error inesperado: ${e.message}")
            }
        }
}