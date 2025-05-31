package rut.uvp.auth.config

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.ReactiveAuthenticationManager
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
import rut.uvp.auth.util.JwtUtil
import kotlin.jvm.optionals.getOrNull

/**
 * Reactive AuthenticationManager that:
 *  1. reads the raw JWT from Authentication.credentials,
 *  2. validates it,
 *  3. fetches the UserEntity on a boundedElastic thread,
 *  4. returns an authenticated token with ROLE_USER.
 *
 * Note: UserRepositoryJpa is blocking; so I therefore wrap calls in
 *       Mono.fromCallable { … }.publishOn(Schedulers.boundedElastic()).
 */
internal class JwtAuthenticationManager(
    private val jwtUtil: JwtUtil,
    private val userRepositoryJpa: UserRepositoryJpa,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val rawToken = authentication.credentials as? String
            ?: return Mono.error(AuthenticationCredentialsNotFoundException("Missing JWT"))

        if (!jwtUtil.validateToken(rawToken)) {
            return Mono.error(BadCredentialsException("Invalid or expired JWT"))
        }

        val userId = jwtUtil.extractUserId(rawToken)
            ?: return Mono.error(BadCredentialsException("Malformed JWT"))

        return Mono.fromCallable { userRepositoryJpa.findById(userId).getOrNull() }
            .publishOn(Schedulers.boundedElastic())
            .switchIfEmpty(Mono.error(BadCredentialsException("User not found")))
            .map { user ->
                UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                )
            }
    }
}
