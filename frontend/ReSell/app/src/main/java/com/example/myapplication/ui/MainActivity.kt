package com.example.myapplication

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
import androidx.navigation.Navigation

import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.navigation.NavigationController
import com.example.myapplication.ui.screen.MainLayout
import com.example.myapplication.ui.components.MySearchBar



import com.example.myapplication.ui.theme.MyApplicationTheme

import dagger.hilt.android.AndroidEntryPoint
import util.Event
import util.EventBus

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {

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