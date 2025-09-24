package com.cerezo.sheshe.filter

import com.cerezo.sheshe.provider.JwtSecretKeyProvider
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.RequiredTypeException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
  @Autowired
  lateinit var jwtSecretKeyProvider: JwtSecretKeyProvider

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val token = extractToken(request)

    if (token != null) {
      val claims = parseClaims(token)
      val authentication = buildAuthentication(claims, request)
      SecurityContextHolder.getContext().authentication = authentication
    }

    filterChain.doFilter(request, response)
  }

  private fun extractToken(request: HttpServletRequest): String? {
    val header = request.getHeader("Authorization")

    return if (header != null && header.startsWith("Bearer ")) {
      header.substring(7)
    } else {
      null
    }
  }

  private fun parseClaims(token: String): Claims =
    Jwts
      .parser()
      .verifyWith(jwtSecretKeyProvider.secretKey)
      .build()
      .parseSignedClaims(token)
      .payload

  private fun buildAuthentication(
    claims: Claims,
    request: HttpServletRequest,
  ): UsernamePasswordAuthenticationToken {
    val role =
      runCatching {
        claims.get("role", String::class.java)
      }.getOrElse {
        when (it) {
          is RequiredTypeException -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
          else -> throw it
        }
      }

    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

    val patientId =
      runCatching {
        UUID.fromString(claims.subject)
      }.getOrElse {
        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token subject")
      }

    return UsernamePasswordAuthenticationToken(
      patientId,
      null,
      authorities,
    ).apply {
      details = WebAuthenticationDetailsSource().buildDetails(request)
    }
  }
}
