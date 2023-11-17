package com.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.movisens.bluetooth.kmm.MainView
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.component.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getKoin().get<PermissionsController>().bind(lifecycle, supportFragmentManager)

        setContent {
            MainView()
        }
    }
}