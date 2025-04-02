package com.example.gomitasmv.presentation.result

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.gomitasmv.databinding.ActivityResultBinding
import com.example.gomitasmv.domain.model.GomitasClassification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[ResultViewModel::class.java]

        // Configurar observadores
        setupObservers()

//        // Configurar listeners
//        setupListeners()

        // Procesar la imagen recibida
        processReceivedImage()
    }

    private fun setupObservers() {
        // Observar el estado de la UI
        viewModel.uiState.observe(this) { state ->
            // Actualizar UI según el estado
            updateUI(state)
        }
    }

    private fun updateUI(state: ResultState) {
        // Mostrar/ocultar carga
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.tvAnalyzing.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // Deshabilitar botones durante la carga
//        binding.btnAnalyzeAgain.isEnabled = !state.isLoading
//        binding.switchEnhance.isEnabled = !state.isLoading
//
//        // Actualizar estado del switch
//        binding.switchEnhance.isChecked = state.enhanceImage

        // Cargar imagen si existe
        state.selectedImageFile?.let {
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(binding.ivPlantImage)
        }

        // Mostrar clasificación si existe
        state.classification?.let { classification ->
            displayClassificationResult(classification)
        }

        // Mostrar error si existe
        state.errorMessage?.let { error ->
            binding.errorProcessingImage.visibility = View.VISIBLE
            binding.errorProcessingImage.text = error
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun displayClassificationResult(classification: GomitasClassification) {
        Log.d("PredictionDebug", "Probabilities: ${classification.allProbabilities}")
        Log.d("PredictionDebug", "Confidence: ${classification.confidence}")

        // Determina la clase con la mayor probabilidad
        val predictedClass = classification.allProbabilities.maxByOrNull { it.value }?.key ?: "Unknown"
        Log.d("PredictionDebug", "Predicted Class: $predictedClass")

        // Actualiza el TextView con la clase predicha
        binding.tvPredictedClass.text = "Resultado: ${predictedClass.lowercase().capitalize()}"

        // Mostrar la confianza de la predicción
        val confidencePercent = (classification.confidence * 100).toInt()
        binding.tvConfidence.text = "Confianza: $confidencePercent%"

        // Establecer la barra de progreso
        binding.progressConfidence.progress = confidencePercent

        // Mostrar el tiempo de procesamiento
        classification.processingTimeSeconds?.let {
            binding.tvProcessingTime.text = "Tiempo: ${it.toFloat()} segundos"
        }

        // Mostrar las probabilidades de todas las clases
        val probabilities = classification.allProbabilities
        val probText = StringBuilder()

        probabilities.forEach { (className, prob) ->
            val probPercent = (prob * 100).toInt()
            probText.append("$className: $probPercent%\n")
        }

        binding.tvAllProbabilities.text = probText.toString()

        // Ocultar errores
        binding.errorProcessingImage.visibility = View.GONE
        binding.noImageReceived.visibility = View.GONE
    }


//    private fun setupListeners() {
//        // Botón para volver a analizar
//        binding.btnAnalyzeAgain.setOnClickListener {
//            // Usar el valor actual del switch de mejora
//            viewModel.predictImage(binding.switchEnhance.isChecked)
//        }
//
//        // Botón para volver a la pantalla principal
//        binding.btnBack.setOnClickListener {
//            finish()
//        }
//
//        // Switch para habilitar/deshabilitar la mejora de imagen
//        binding.switchEnhance.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.setEnhanceImage(isChecked)
//        }
//    }

    private fun processReceivedImage() {
        // Obtener la URI de la imagen desde el intent
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)

        if (imageUriString != null) {
            val uri = Uri.parse(imageUriString)

            // Convertir la URI a un archivo que podemos enviar a la API
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val file = uriToFile(uri)
                    withContext(Dispatchers.Main) {
                        viewModel.setSelectedImageFile(file)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.errorProcessingImage.visibility = View.VISIBLE
                        binding.errorProcessingImage.text = "Error al procesar la imagen: ${e.message}"
                        Toast.makeText(
                            this@ResultActivity,
                            "Error al procesar la imagen: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else {
            binding.noImageReceived.visibility = View.VISIBLE
            Toast.makeText(this, "No se recibió ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun uriToFile(uri: Uri): File = withContext(Dispatchers.IO) {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw Exception("No se pudo abrir la imagen")

        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        try {
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            file
        } catch (e: Exception) {
            throw Exception("Error al copiar la imagen: ${e.message}")
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}