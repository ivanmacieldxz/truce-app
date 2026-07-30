package com.ivanmacieldxz.truce.domain.repository

class AuthException(
    val userMessage: String,
    val debugMessage: String? = null
) : Exception(userMessage)
