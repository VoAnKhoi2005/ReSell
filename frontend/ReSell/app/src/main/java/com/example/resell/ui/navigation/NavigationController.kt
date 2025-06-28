package com.example.resell.ui.navigation

import RegionSelection
import androidx.navigation.NavController

object NavigationController {
    lateinit var navController: NavController

    //test để làm vụ filter địa chỉ
    var sharedRegionSelection: RegionSelection = RegionSelection()
}