package com.aritradas.medai.domain.repository

import com.aritradas.medai.utils.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun signInWithGoogle(idToken: String): Resource<AuthResult>

    suspend fun signOut(): Resource<Unit>

    fun getCurrentUser(): FirebaseUser?
}
