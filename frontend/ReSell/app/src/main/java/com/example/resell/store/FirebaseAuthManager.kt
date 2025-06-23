package com.example.resell.store

import android.app.Application
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.example.resell.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.example.resell.model.LoginResponse
import com.example.resell.model.User
import com.example.resell.repository.UserRepository
import kotlinx.coroutines.coroutineScope

@Singleton
class FirebaseAuthManager @Inject constructor(
    private val application: Application,
    private val userRepo: UserRepository
) {
    lateinit var auth: FirebaseAuth

    suspend fun handleSignIn(result: GetCredentialResponse): String {
        var fbIDToken = ""

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        fbIDToken = googleIdTokenCredential.idToken
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("Firebase:", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("Firebase:", "Unexpected type of credential")
                }
            }

            else -> {
                Log.e("Firebase:", "Unexpected type of credential")
            }
        }

        return fbIDToken
    }
}