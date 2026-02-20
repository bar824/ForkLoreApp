
package com.example.forklore.utils

import com.google.firebase.firestore.DocumentSnapshot

sealed class Resource<T>(val data: T? = null, val message: String? = null, val lastVisible: DocumentSnapshot? = null) {
    class Success<T>(data: T, lastVisible: DocumentSnapshot? = null) : Resource<T>(data, lastVisible = lastVisible)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
