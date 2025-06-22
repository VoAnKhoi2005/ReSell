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
    object CategorySelection: Screen("category_selection_screen")
    object AddPost: Screen("add_post_screen")
    object BuyingOrder: Screen("buying_order_screen")
    object MyOrder: Screen("my_order_screen")
    object Payment: Screen("payment_screen")
    object AddressSetup: Screen("address_setup_screen")
    object AddressAdd: Screen("address_add_screen")
    object ProvinceSelect: Screen("province_select_screen")
    object DistrictSelect : Screen("district_select_screen")
    object WardSelect : Screen("ward_select_screen")
    object Favorite: Screen("favorite_screen")
    object Rating: Screen("rating_screen")
    object PhoneRegister: Screen("phone_register_screen")


}