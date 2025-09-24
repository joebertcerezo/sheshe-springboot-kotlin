package com.cerezo.sheshe.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.nio.charset.StandardCharsets.UTF_8

@Service
class EmailService(
  private val mailSender: JavaMailSender,
  private val templateEngine: SpringTemplateEngine,
) {
  @Value("\${email.domain}")
  lateinit var domain: String

  @Value("\${email.from}")
  lateinit var from: String

  fun sendEmail() {
    val context =
      Context().apply {
        setVariables(
          mapOf(
            "domain" to domain,
            "otp" to "123",
            "expiryMinutes" to "123expiry",
          ),
        )
      }
    val mimeMessage: MimeMessage = mailSender.createMimeMessage()
    MimeMessageHelper(mimeMessage, true, UTF_8.name()).apply {
      setText(templateEngine.process("send-email", context), true)
      setTo("hackdog195@gmail.com")
      setSubject("Password Reset OTP")
      setFrom("$from <$domain>")
      setReplyTo(from)
    }
    mailSender.send(mimeMessage)
  }
}
