package com.ivanmacieldxz.truce.domain.util

import android.util.Patterns

object AuthValidator {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun hasPasswordMinLength(password: String): Boolean = password.length >= 8
    fun hasPasswordUppercase(password: String): Boolean = password.any { it.isUpperCase() }
    fun hasPasswordLowercase(password: String): Boolean = password.any { it.isLowerCase() }
    fun hasPasswordDigit(password: String): Boolean = password.any { it.isDigit() }

    fun isValidPassword(password: String): Boolean {
        return hasPasswordMinLength(password) &&
                hasPasswordUppercase(password) &&
                hasPasswordLowercase(password) &&
                hasPasswordDigit(password)
    }

    // Min 5 chars, only lowercase, digits, dot and underscore
    fun isValidUsername(username: String): Boolean {
        val regex = "^[a-z0-9._]{5,}$".toRegex()
        return username.matches(regex)
    }
    
    fun formatUsername(input: String): String {
        return input.lowercase().filter { it.isLetterOrDigit() || it == '.' || it == '_' }
    }

    fun isValidFullName(fullName: String): Boolean {
        return fullName.trim().length >= 2
    }
}
