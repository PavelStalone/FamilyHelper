package rut.uvp.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rut.uvp.auth.domain.model.AuthResponse
import rut.uvp.auth.domain.model.LoginRequest
import rut.uvp.auth.domain.model.RegisterRequest
import rut.uvp.auth.infrastructure.entity.UserEntity
import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
import rut.uvp.auth.util.JwtUtil

@Service
internal class AuthService(
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepositoryJpa,
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        require(!userRepository.existsByEmail(request.email)) { "Email already in use" }

        val hashedPassword = passwordEncoder.encode(request.password)
        val user = userRepository.save(
            UserEntity(
                name = request.name,
                email = request.email,
                passwordHash = hashedPassword,
            )
        )

        val token = jwtUtil.generateToken(user.id, user.email)
        return AuthResponse(token = token, userId = user.id)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = requireNotNull(userRepository.findByEmail(request.email)) { "Invalid credentials" }
        require(passwordEncoder.matches(request.password, user.passwordHash)) { "Invalid credentials" }

        val token = jwtUtil.generateToken(user.id, user.email)
        return AuthResponse(token, user.id)
    }
}
