package rut.uvp.auth.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
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
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse {
        return authService.login(request)
    }
}
