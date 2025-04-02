package com.example.gomitasmv

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.gomitasmv.core.navigation.NavigationWrapper
import com.example.gomitasmv.presentation.MainState
import com.example.gomitasmv.presentation.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    // Referencias a vistas
    private lateinit var progressBar: ProgressBar
    private lateinit var tvApiStatus: TextView
    private lateinit var tvAvailableClasses: TextView
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button

    // Registro para solicitar permisos de cámara
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermitted = permissions[Manifest.permission.CAMERA] ?: false
        val storagePermitted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }

        if (cameraPermitted && storagePermitted) {
            NavigationWrapper.navigateToCamera(this)
        } else {
            Toast.makeText(
                this,
                "Se necesitan permisos de cámara y almacenamiento para continuar",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Registro para seleccionar imagen de la galería
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                NavigationWrapper.navigateToResult(this, uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar referencias a vistas
        progressBar = findViewById(R.id.progressBar)
        tvApiStatus = findViewById(R.id.tvApiStatus)
        tvAvailableClasses = findViewById(R.id.tvAvailableClasses)
        btnCamera = findViewById(R.id.btnCamera)
        btnGallery = findViewById(R.id.btnGallery)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Configurar observadores
        setupObservers()

        // Configurar listeners
        setupListeners()

        // Comprobar estado de la API
        viewModel.checkApiHealth()
    }

    private fun setupObservers() {
        // Observar estado UI
        viewModel.uiState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: MainState) {
        // Manejo de carga
        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // Estado de la API
//        tvApiStatus.text = state.apiStatusText
//        tvApiStatus.setTextColor(
//            ContextCompat.getColor(
//                this,
//                if (state.isApiConnected) R.color.green else R.color.red
//            )
//        )

        // Clases disponibles
        tvAvailableClasses.text = state.availableClassesText
    }

    private fun setupListeners() {
        // Botón para abrir la cámara
        btnCamera.setOnClickListener {
            checkAndRequestPermissions()
        }

        // Botón para seleccionar imagen de la galería
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }
    }

    private fun checkAndRequestPermissions() {
        val cameraPermission = Manifest.permission.CAMERA
        val readStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val permissionsToRequest = mutableListOf<String>()

        // Verificar permisos de cámara
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(cameraPermission)
        }

        // Verificar permisos de lectura
        if (ContextCompat.checkSelfPermission(this, readStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(readStoragePermission)
        }

        // Verificar permisos de escritura (para Android < 10)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, writeStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(writeStoragePermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            cameraPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Ya tenemos todos los permisos
            NavigationWrapper.navigateToCamera(this)
        }
    }
}