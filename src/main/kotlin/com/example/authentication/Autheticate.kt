package com.example.authentication

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

//hash key
private val hashKey = System.getenv("HASH_SECRET_KEY").toByteArray()

// algorithm to generate HashKey
private val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

// its returns the hashString generated
fun hash(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}

/**
 * We are storing hash password in database and not plaintext password
 */