package com.example.myapplication.ui.screen

import android.service.controls.Control
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.BottomBar
import com.example.myapplication.ui.components.bottomNavItems

@Composable
fun MainLayout(modifier: Modifier = Modifier, navController: NavController) {
var selectedIndex by remember { mutableStateOf(bottomNavItems[0])}
    Scaffold(
        bottomBar = {
          BottomBar(
              items = bottomNavItems,
              selectedItem = selectedIndex,
              onItemClick = {selectedIndex = it}//navigate ở đây
          )
        }
    ) {
        ContentScreen(modifier=modifier.padding(it))

    }
}

@Composable
fun ContentScreen(modifier: Modifier) {
//nơi chuyển hướng
}
