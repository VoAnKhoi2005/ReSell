package com.example.resell.ui.viewmodel.auth.register
import androidx.lifecycle.ViewModel
import com.example.resell.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel  @Inject constructor(
    private val myRepository: UserRepository
) : ViewModel(){

}