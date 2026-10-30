package com.example.musicplayer.data.repositoryImpl

import android.util.Log
import com.example.musicplayer.domain.model.User
import com.example.musicplayer.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : UserRepository {

    override fun getUserData(): Flow<User> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            close(IllegalStateException("Пользователь не зарегистрирован"))
            return@callbackFlow
        }

        val listenerRegistration = firestore
            .collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(user)
                    }
                } else {
                    Log.e("TrackRepo", "Документ пользователя $uid не найден в Firestore")
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

}