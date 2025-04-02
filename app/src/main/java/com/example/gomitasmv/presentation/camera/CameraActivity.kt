package com.example.gomitasmv.presentation.camera

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.gomitasmv.R
import com.example.gomitasmv.core.navigation.NavigationWrapper
import com.example.gomitasmv.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var viewModel: CameraViewModel

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        // Configurar directorio de salida para imágenes
        outputDirectory = getOutputDirectory()

        // Inicializar executor para las operaciones de cámara
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Configurar observadores
        setupObservers()

        // Configurar listeners
        setupListeners()

        // Iniciar la cámara
        startCamera()
    }

    private fun setupObservers() {
        // Observar el estado de la UI
        viewModel.uiState.observe(this) { state ->
            // Mostrar/ocultar carga
            binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            when (state.cameraStatus) {
                CameraStatus.IDLE -> {
                    binding.btnCapture.isEnabled = true
                }
                CameraStatus.CAPTURING -> {
                    binding.btnCapture.isEnabled = false
                }
                CameraStatus.IMAGE_CAPTURED -> {
                    // Obtener la URI de la imagen capturada
                    state.capturedImageUri?.let { uri ->
                        navigateToResult(uri)
                    }
                }
                CameraStatus.ERROR -> {
                    binding.btnCapture.isEnabled = true
                    binding.captureError.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        "Error al capturar la imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupListeners() {
        // Botón para capturar imagen
        binding.btnCapture.setOnClickListener {
            takePicture()
        }

        // Botón para cerrar la cámara
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                // Obtener el proveedor de cámara
                val cameraProvider = cameraProviderFuture.get()

                // Configurar preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

                // Configurar captura de imagen
                imageCapture = ImageCapture.Builder().build()

                // Seleccionar cámara trasera como predeterminada
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Vincular los casos de uso a la cámara
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error al iniciar cámara", e)
                binding.cameraError.visibility = View.VISIBLE
                Toast.makeText(this, "Error al iniciar la cámara", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture() {
        // Verificar que imageCapture está inicializado
        val imageCapture = imageCapture ?: return

        // Actualizar estado a CAPTURING
        viewModel.updateCameraStatus(CameraStatus.CAPTURING)

        // Crear el archivo de salida
        val photoFile = createFile(outputDirectory)
        viewModel.setTempImageFile(photoFile)

        // Configurar opciones de salida
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capturar la imagen
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Guardar URI de la imagen capturada
                    val savedUri = Uri.fromFile(photoFile)
                    viewModel.setCapturedImageUri(savedUri)

                    // Actualizar estado a IMAGE_CAPTURED
                    viewModel.updateCameraStatus(CameraStatus.IMAGE_CAPTURED)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error al guardar imagen", exception)

                    // Actualizar estado a ERROR
                    viewModel.updateCameraStatus(CameraStatus.ERROR)
                }
            }
        )
    }

    private fun navigateToResult(imageUri: Uri) {
        NavigationWrapper.navigateToResult(this, imageUri)
        finish()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun createFile(baseFolder: File): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        return File(baseFolder, "IMG_${timeStamp}.jpg")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraActivity"
    }
}