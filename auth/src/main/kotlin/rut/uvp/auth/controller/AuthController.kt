package rut.uvp.auth.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import rut.uvp.auth.domain.model.AuthResponse
import rut.uvp.auth.domain.model.LoginRequest
import rut.uvp.auth.domain.model.RegisterRequest
import rut.uvp.auth.service.AuthService
import jakarta.validation.Valid

@RestController
@RequestMapping("/auth")
internal class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): Mono<AuthResponse> {
        return Mono.just(authService.register(request))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): Mono<AuthResponse> {
        return Mono.just(authService.login(request))
    }
}
