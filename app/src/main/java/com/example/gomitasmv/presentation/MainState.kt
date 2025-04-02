package com.example.gomitasmv.presentation

 // Estado de la UI para la pantalla principal

data class MainState(
    val isLoading: Boolean = false,
    val isApiConnected: Boolean = false,
    val apiStatusText: String = "Estado de la API: Verificando...",
    val availableClasses: List<String> = emptyList(),
    val availableClassesText: String = "Clases disponibles: Cargando...",
    val errorMessage: String? = null
)