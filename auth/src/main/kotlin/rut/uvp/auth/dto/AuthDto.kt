package rut.uvp.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// Запрос на регистрацию
data class RegisterRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val action: String, // "create" | "join"
    val familyCode: String? = null,
    val relation: String? = null,
)

// Запрос на вход
data class LoginRequest(
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
)

// Ответ с токеном
data class AuthResponse(
    val token: String,
    val userId: Long,
    val familyId: Long?,
    val familyCode: String?,
) 