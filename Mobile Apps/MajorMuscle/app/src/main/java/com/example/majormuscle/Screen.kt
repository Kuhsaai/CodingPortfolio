package com.example.majormuscle

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Exercises : Screen("exercises")
    object Alerts : Screen("alerts")
    object Info : Screen("info")
}
