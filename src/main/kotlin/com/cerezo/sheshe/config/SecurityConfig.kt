package com.cerezo.sheshe.config

import com.cerezo.sheshe.filter.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {
  @Value("\${cors.allowed.origins}")
  lateinit var allowedOrigins: String

  @Bean
  fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
    val configuration = CorsConfiguration()

    // Allow requests from specific origins
    configuration.allowedOrigins = allowedOrigins.split(",").map { it.trim() }

    // Allow credentials (cookies, authorization headers, etc.)
    configuration.allowCredentials = true

    // Allow specific HTTP methods
    configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")

    // Allow specific headers
    configuration.allowedHeaders = listOf("Authorization", "Content-Type")

    // Apply CORS settings to all paths
    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", configuration)

    return source
  }

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .cors { }
      .securityMatcher("/api/v1/**")
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .authorizeHttpRequests { auth ->
        auth.requestMatchers("/api/v1/auth/**", "/").permitAll()
        auth.anyRequest().authenticated()
      }.sessionManagement { session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }

    return http.build()
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
