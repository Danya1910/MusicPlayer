package com.example.musicplayer.data.repositoryImpl

import android.util.Log
import com.example.musicplayer.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

        Log.d("AuthRepository", "результат входа: $result")

        Unit
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
    ): Result<Unit> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(
            email,
            password
        ).await()

        val userCreatedUid = result.user?.uid ?: throw Exception("Ошибка создания пользователя")

        val user = mapOf(
            "name" to name,
            "uid" to userCreatedUid,
            "avatarUrl" to "",
        )

        firestore.collection("users").document(userCreatedUid).set(user).await()

        Log.d("AuthRepository", "Юзер успешно создан")

        Unit
    }

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

}