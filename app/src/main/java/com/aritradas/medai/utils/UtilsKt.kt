package com.aritradas.medai.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

object UtilsKt {

    fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun validateName(name: String?): Boolean {
        return !name.isNullOrEmpty() &&
                name.split(" ")
                    .all { word -> word.isNotEmpty() && Pattern.matches("^([A-Za-z.]+)*", word) }
    }

    fun validatePassword(password: String): Boolean {
        // Check if the password is empty
        if (password.isEmpty()) {
            return false
        }

        // Check if the password is at least 8 characters long
        if (password.length < 8) {
            return false
        }

        // Check if the password contains at least one uppercase letter
        if (!password.contains(Regex("[A-Z]"))) {
            return false
        }

        // Check if the password contains at least one lowercase letter
        if (!password.contains(Regex("[a-z]"))) {
            return false
        }

        // Check if the password contains at least one digit
        if (!password.contains(Regex("\\d"))) {
            return false
        }

        // Check if the password contains at least one special character
        if (!password.contains(Regex("[^A-Za-z0-9]"))) {
            return false
        }

        // Check if the password does not contain any whitespace
        if (password.contains(" ")) {
            return false
        }

        return true
    }

    fun validateEmail(email: String?): Boolean {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun calculatePasswordStrength(password: String): Int {
        var score = 0
        // Check for password length
        if (password.length >= 8) {
            score += 1
        }
        // Check for uppercase letters
        if (password.matches(Regex(".*[A-Z].*"))) {
            score += 1
        }
        // Check for lowercase letters
        if (password.matches(Regex(".*[a-z].*"))) {
            score += 1
        }
        // Check for digits
        if (password.matches(Regex(".*\\d.*"))) {
            score += 1
        }
        // Check for special characters
        if (password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))) {
            score += 1
        }
        // Check for consecutive characters
        if (!password.matches(Regex("(.)\\1{2,}"))) {
            score += 1
        }
        // Calculate the password score out of 9
        return ((score.toFloat() / 6.toFloat()) * 9).toInt()
    }

    fun generateRandomPassword(length: Int): String {
        val charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    fun calculateAllPasswordsScore(password: String): Int {

        var score = 0

        // Check for password length
        if (password.length >= 8) {
            score += 10
        }

        // Check for uppercase letters
        if (password.matches(Regex(".*[A-Z].*"))) {
            score += 10
        }

        // Check for lowercase letters
        if (password.matches(Regex(".*[a-z].*"))) {
            score += 1
        }

        // Check for digits
        if (password.matches(Regex(".*\\d.*"))) {
            score += 1
        }

        // Check for special characters
        if (password.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))) {
            score += 1
        }

        // Check for consecutive characters
        if (!password.matches(Regex("(.)\\1{2,}"))) {
            score += 1
        }

        // Calculate the password score out of 100
        return (score / 6) * 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateThresholdDate(): String {
        val thirtyDaysAgo = LocalDate.now().minusDays(30)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return thirtyDaysAgo.format(formatter)
    }

    fun Context?.isNetworkAvailable(): Boolean {
        if (this == null) return false
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}