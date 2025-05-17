package rut.uvp.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["rut.uvp.core.data.repository"])
class JpaConfig 