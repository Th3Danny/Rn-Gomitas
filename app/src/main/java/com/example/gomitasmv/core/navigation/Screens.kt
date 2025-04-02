package com.example.buttonia.core.navigation

sealed class Screens(val route: String) {
    object Main : Screens("main_screen")
    object Camera : Screens("camera_screen")
    object Result : Screens("result_screen")
}