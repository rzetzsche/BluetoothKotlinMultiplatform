package com.movisens.bluetooth.kmm

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.movisens.bluetooth.kmm.view.MainScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(MainScreen())
    }
}