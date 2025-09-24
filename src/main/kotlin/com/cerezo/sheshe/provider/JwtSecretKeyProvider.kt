package com.cerezo.sheshe.provider

import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtSecretKeyProvider {
  @Value("\${jwt.secret.key}")
  private lateinit var secret: String

  val jwtSecret: String
    get() = secret
  final lateinit var secretKey: SecretKey
    private set

  companion object {
    private const val MIN_BYTE_ARRAY_SIZE = 32
  }

  @PostConstruct
  fun init() {
    secretKey = generateSecretKey(secret)
  }

  private fun generateSecretKey(secret: String): SecretKey {
    val keyBytes = secret.toByteArray(Charsets.UTF_8)

    val paddedKeyBytes =
      if (keyBytes.size < MIN_BYTE_ARRAY_SIZE) {
        keyBytes.copyOf(MIN_BYTE_ARRAY_SIZE)
      } else {
        keyBytes
      }

    return Keys.hmacShaKeyFor(paddedKeyBytes)
  }
}
