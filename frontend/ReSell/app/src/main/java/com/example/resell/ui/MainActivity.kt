package com.example.resell.ui

import SetupNavGraph

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController

import androidx.navigation.compose.rememberNavController
import com.example.resell.repository.AddressRepository
import com.example.resell.ui.components.HideNavigationBar
import com.example.resell.ui.navigation.NavigationController


import com.example.resell.ui.theme.MyApplicationTheme

import dagger.hilt.android.AndroidEntryPoint
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HideNavigationBar()

                val lifecycle = LocalLifecycleOwner.current.lifecycle
                LaunchedEffect(key1 = lifecycle) {
                    repeatOnLifecycle(state = Lifecycle.State.STARTED){
                        EventBus.events.collect{ event ->
                            if (event is Event.Toast){
                                Toast.makeText(this@MainActivity,event.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    navController = rememberNavController()
                    SetupNavGraph(navController = navController)
                    NavigationController.navController = navController
                }
            }
        }
    }

}