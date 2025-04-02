package com.example.gomitasmv.data.repositorie

import com.example.gomitasmv.data.datasource.RemoteDataSource
import com.example.gomitasmv.data.model.ApiResult
import com.example.gomitasmv.data.model.ClassifierResponse
import com.example.gomitasmv.data.model.ClassesResponse
import com.example.gomitasmv.data.model.HealthResponse
import java.io.File

class ClassifierRepository {

    private val remoteDataSource = RemoteDataSource()


     // Comprueba si la API está activa

    suspend fun checkApiHealth(): ApiResult<HealthResponse> {
        return remoteDataSource.checkApiHealth()
    }


     // Obtiene las clases disponibles para clasificación

    suspend fun getAvailableClasses(): ApiResult<ClassesResponse> {
        return remoteDataSource.getAvailableClasses()
    }


     // Envía una imagen para clasificación

    suspend fun predictImage(imageFile: File, enhance: Boolean = false): ApiResult<ClassifierResponse> {
        return remoteDataSource.predictImage(imageFile, enhance)
    }
}