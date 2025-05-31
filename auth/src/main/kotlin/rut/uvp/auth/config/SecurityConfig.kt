package rut.uvp.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
import rut.uvp.auth.util.JwtUtil

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
internal class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepositoryJpa,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager =
        JwtAuthenticationManager(jwtUtil, userRepository)

    @Bean
    fun bearerTokenConverter() = BearerTokenServerAuthenticationConverter()

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        authManager: ReactiveAuthenticationManager,
        converter: BearerTokenServerAuthenticationConverter,
    ): SecurityWebFilterChain {

        val jwtFilter = AuthenticationWebFilter(authManager).apply {
            setServerAuthenticationConverter(converter)
            setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        }

        return http
            .csrf { it.disable() }
            .cors { }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange { auth ->
                auth.pathMatchers("/auth/**", "/error").permitAll()
                    .anyExchange().authenticated()
            }
            .build()
    }
}
