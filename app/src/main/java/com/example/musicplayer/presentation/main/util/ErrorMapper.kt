package com.example.musicplayer.presentation.main.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toToastData(): ToastData {
    return when (this) {
        is UnknownHostException,
        is IOException,
        is FirebaseNetworkException -> ToastData(
            title = "Exception",
            message = "Please check your internet connection and try again.",
            type = ToastType.ERROR
        )

        is SocketTimeoutException -> ToastData(
            title = "Exception",
            message = "Connection timed out. Please try again later.",
            type = ToastType.ERROR
        )

        is FirebaseFirestoreException -> when (this.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE -> ToastData(
                title = "Exception",
                message = "Service is temporarily unavailable or you are offline.",
                type = ToastType.ERROR
            )
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> ToastData(
                title = "Exception",
                message = "You do not have permission to view this data.",
                type = ToastType.ERROR
            )
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> ToastData(
                title = "Exception",
                message = "Please sign in to your account again.",
                type = ToastType.ERROR
            )
            else -> ToastData(
                title = "Exception",
                message = "Failed to load data. Code: ${this.code.name}",
                type = ToastType.ERROR
            )
        }

        else -> ToastData(
            title = "Error",
            message = "An unknown error occurred.",
            type = ToastType.ERROR
        )
    }
}