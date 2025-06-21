package com.example.resell.ui.navigation

sealed class Screen(val route: String){
    object Login: Screen(route = "login_screen")
    object Register: Screen(route = "register_screen")
    object ChatHome: Screen(route = "chathome-screen")
    object Main: Screen("main_screen") // chứa BottomBar
    object Home: Screen("home_screen")
    object Manage: Screen("manage_screen")
    object Post: Screen("post_screen")
    object Profile: Screen("profile_screen")
    object Market: Screen("market_screen")
    object Search: Screen("search_screen")
    object ProfileDetail: Screen("profiledetail_screen")
    object ProductDetail: Screen("productdetail_screen")
    object PhoneAuth: Screen("phoneAuth_screen")
    object Add: Screen("add_screen")
    object BuyingOrder: Screen("buying_order")
    object MyOrder: Screen("my_order")
    object Payment: Screen("payment")
    object AddressSetup: Screen("address_setup")
    object AddressAdd: Screen("address_add")
    object ProvinceSelect: Screen("province_select")
    object DistrictSelect : Screen("district_select")
    object WardSelect : Screen("ward_select")


}