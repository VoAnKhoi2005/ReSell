package com.example.resell.network


import android.util.Log
import com.example.resell.model.ErrorResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException

//fun Throwable.toNetworkError(): NetworkError {
//    val error = when (this) {
//        is IOException -> ApiError.NetworkError
//        is HttpException -> ApiError.UnknownResponse
//        else -> ApiError.UnknownError
//    }
//    return NetworkError(
//        error = error,
//        t = this
//    )
//}

val moshi: Moshi by lazy {
    Moshi.Builder().build()
}

val errorAdapter: JsonAdapter<ErrorResponse> by lazy {
    moshi.adapter(ErrorResponse::class.java)
}

fun Throwable.toNetworkError(): NetworkError {
    return when (this) {
        //Happens if there's no internet, timeout, etc.
        is IOException -> NetworkError(
            error = ApiError.NetworkError,
            message = this.localizedMessage ?: ApiError.NetworkError.defaultMessage,
            t = this
        )

        //Server error (4xx or 5xx)
        is HttpException -> {
            val code = this.code()
            val errorBody = this.response()?.errorBody()?.string()
            Log.d("NetworkError", "HTTP $code error body: $errorBody")
            val errorResponse = parseErrorBody(errorBody)

            NetworkError(
                code = code,
                error = ApiError.UnknownResponse,
                message = errorResponse?.error ?: "Server error (code $code)",
                errors = errorResponse?.errors,
                t = this
            )
        }

        else -> NetworkError(
            error = ApiError.UnknownError,
            message = this.localizedMessage ?: ApiError.UnknownError.defaultMessage,
            t = this
        )
    }
}

fun parseErrorBody(json: String?): ErrorResponse? {
    if (json.isNullOrBlank())
        return null

    return try {
        errorAdapter.fromJson(json)
    } catch (e: Exception) {
        null
    }
}