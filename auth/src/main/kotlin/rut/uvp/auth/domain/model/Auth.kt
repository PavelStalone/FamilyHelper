package rut.uvp.auth.domain.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// Запрос на регистрацию
internal data class RegisterRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
    @field:NotBlank
    val name: String,
)

// Запрос на вход
internal data class LoginRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
)

// Ответ с токеном
internal data class AuthResponse(
    val token: String,
    val userId: Long,
)
