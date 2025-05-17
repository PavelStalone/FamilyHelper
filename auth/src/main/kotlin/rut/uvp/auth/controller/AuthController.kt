package rut.uvp.auth.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import rut.uvp.auth.dto.AuthResponse
import rut.uvp.auth.dto.LoginRequest
import rut.uvp.auth.dto.RegisterRequest
import rut.uvp.auth.service.AuthService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthResponse {
        return authService.login(request)
    }
} 