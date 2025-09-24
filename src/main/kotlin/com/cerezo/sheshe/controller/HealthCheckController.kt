package com.cerezo.sheshe.controller

import com.cerezo.sheshe.service.EmailService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController(
  private val emailService: EmailService,
) {
  @GetMapping
  fun index(): ResponseEntity<String> {
    emailService.sendEmail()
    return ResponseEntity.status(HttpStatus.OK).body("Server is running...")
  }
}
