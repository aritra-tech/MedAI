package com.aritradas.medai.ui.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aritradas.medai.domain.model.User
import com.aritradas.medai.utils.UtilsKt.validateEmail
import com.aritradas.medai.utils.runIO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import timber.log.Timber

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth
    val resetPassword = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()
    val registerStatus = MutableLiveData<Boolean>()
    val loginSuccess = MutableLiveData<Boolean>()

    fun resetPassword(email: String) = runIO {
        val trimmedEmail = email.trim()

        if (!validateEmail(trimmedEmail)) {
            errorLiveData.postValue("Please enter a valid email address")
            return@runIO
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(trimmedEmail)
            .addOnSuccessListener {
                resetPassword.postValue(true)
            }.addOnFailureListener {
                errorLiveData.postValue(it.message.toString())
            }
    }

    fun logIn(email: String, password: String) = runIO {
        val trimmedEmail = email.trim()

        // Validate inputs
        if (trimmedEmail.isEmpty()) {
            errorLiveData.postValue("Email cannot be empty")
            return@runIO
        }

        if (!validateEmail(trimmedEmail)) {
            errorLiveData.postValue("Please enter a valid email address")
            return@runIO
        }

        if (password.isEmpty()) {
            errorLiveData.postValue("Password cannot be empty")
            return@runIO
        }

        delay(2000L)
        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginSuccess.postValue(true)
                } else {
                    errorLiveData.postValue("Invalid email or password")
                    loginSuccess.postValue(false)
                }
            }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        onSignedUp: (FirebaseUser) -> Unit,
    ) = runIO {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        // Validate inputs before proceeding
        when {
            trimmedName.isEmpty() -> {
                errorLiveData.postValue("Name cannot be empty")
                return@runIO
            }
            trimmedEmail.isEmpty() -> {
                errorLiveData.postValue("Email cannot be empty")
                return@runIO
            }
            !validateEmail(trimmedEmail) -> {
                errorLiveData.postValue("Please enter a valid email address")
                return@runIO
            }
            password.isEmpty() -> {
                errorLiveData.postValue("Password cannot be empty")
                return@runIO
            }
            password.length < 6 -> {
                errorLiveData.postValue("Password must be at least 6 characters long")
                return@runIO
            }
        }

        delay(2000L)
        Timber.tag("AuthViewModel").d("Attempting to create user with email: '$trimmedEmail'")

        auth.createUserWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val userProfile = User(trimmedName, trimmedEmail)

                    val userDB = FirebaseFirestore.getInstance()
                    user?.let {
                        userDB.collection("users")
                            .document(it.uid)
                            .set(userProfile)
                            .addOnSuccessListener {
                                Timber.tag("AuthViewModel")
                                    .d("User profile is successfully created for user %s", user.uid)
                                onSignedUp(user)
                                registerStatus.postValue(true)
                            }
                            .addOnFailureListener { exception ->
                                Timber.tag("AuthViewModel")
                                    .e("Failed to create user profile: %s", exception.message)
                                errorLiveData.postValue("Failed to create user profile: ${exception.message}")
                            }
                    } ?: run {
                        errorLiveData.postValue("User is null after creation")
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error occurred"
                    Timber.tag("AuthViewModel").e("Sign up failed: %s", errorMessage)
                    errorLiveData.postValue("Sign up failed: $errorMessage")
                }
            }
    }
}