package com.example.gomitasmv.core.navigation


import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.gomitasmv.presentation.camera.CameraActivity
import com.example.gomitasmv.presentation.result.ResultActivity


// En NavigationWrapper.kt
object NavigationWrapper {
    fun navigateToCamera(context: Context) {
        val intent = Intent(context, CameraActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToResult(context: Context, imageUri: Uri) {
        val intent = Intent(context, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString())
            // Añadir esta bandera para asegurar que la actividad se inicie correctamente
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    }
}
