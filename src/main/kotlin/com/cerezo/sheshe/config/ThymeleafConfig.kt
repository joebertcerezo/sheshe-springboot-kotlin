package com.cerezo.sheshe.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

@Configuration
class ThymeleafConfig {
  @Bean
  fun templateEngine(): SpringTemplateEngine {
    val templateEngine = SpringTemplateEngine()
    val templateResolver =
      ClassLoaderTemplateResolver().apply {
        prefix = "/templates/"
        suffix = ".html"
        templateMode = TemplateMode.HTML
        characterEncoding = "UTF-8"
        isCacheable = true
      }
    templateEngine.setTemplateResolver(templateResolver)
    return templateEngine
  }
}
