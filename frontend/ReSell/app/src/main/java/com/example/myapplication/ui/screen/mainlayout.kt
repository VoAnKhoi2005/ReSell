package com.example.myapplication.ui.screen

import android.service.controls.Control
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.BottomBar
import com.example.myapplication.ui.components.TopBar
import com.example.myapplication.ui.components.bottomNavItems
import com.example.myapplication.ui.screen.home.HomeScreen
import kotlinx.serialization.Serializable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(modifier: Modifier = Modifier, navController: NavController) {
    var selectedIndex by remember { mutableStateOf(bottomNavItems[0])}

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    Scaffold(
        bottomBar = {
          BottomBar(
              items = bottomNavItems,
              selectedItem = selectedIndex,
              onItemClick = {selectedIndex = it

              }//navigate ở đây
          )
        }


    ) {
        ContentScreen(modifier=modifier.padding(it),navController)

    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, navController: NavController) {
  HomeScreen()
}
