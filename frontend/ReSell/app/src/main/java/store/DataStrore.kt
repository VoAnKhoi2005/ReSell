package store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.User

object DataStore {
    var user by mutableStateOf<User?>(null)
    val locationMessageKey = "dab64614f35cbb2e3d8819ef6c1769e4"
}
