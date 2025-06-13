package store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.User

object DataStore {
    var user by mutableStateOf<User?>(null)
}
