package rut.uvp.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rut.uvp.auth.dto.AuthResponse
import rut.uvp.auth.dto.LoginRequest
import rut.uvp.auth.dto.RegisterRequest
import rut.uvp.auth.util.JwtUtil
import rut.uvp.core.common.log.Log
import rut.uvp.core.data.entity.Family
import rut.uvp.core.data.entity.FamilyMember
import rut.uvp.core.data.entity.User
import rut.uvp.core.data.repository.FamilyMemberRepository
import rut.uvp.core.data.repository.FamilyRepository
import rut.uvp.core.data.repository.UserRepository

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val familyRepository: FamilyRepository,
    private val familyMemberRepository: FamilyMemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        require(request.action in setOf("create", "join")) { "Invalid action" }

        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already in use")
        }

        val hashedPassword = passwordEncoder.encode(request.password)
        val user = userRepository.save(User(email = request.email, passwordHash = hashedPassword, name = request.name))

        val family: Family = when (request.action) {
            "create" -> createFamily(user)
            "join" -> joinFamily(user, request)
            else -> error("Unhandled action")
        }

        val token = jwtUtil.generateToken(user.id, user.email)
        return AuthResponse(token = token, userId = user.id, familyId = family.id, familyCode = family.familyCode)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email) ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        val familyMember = familyMemberRepository.findFirstByUserId(user.id)
        val familyId = familyMember?.familyId
        val familyCode = familyId?.let { id -> familyRepository.findById(id).orElse(null)?.familyCode }
        val token = jwtUtil.generateToken(user.id, user.email)
        return AuthResponse(token, user.id, familyId, familyCode)
    }

    private fun createFamily(user: User): Family {
        var code: String
        do {
            code = generateFamilyCode()
        } while (familyRepository.existsByFamilyCode(code))

        val family = familyRepository.save(Family(familyCode = code, creatorId = user.id))
        familyMemberRepository.save(
            FamilyMember(userId = user.id, familyId = family.id, relation = "creator")
        )
        Log.i("Family created with code=$code by user=${user.id}")
        return family
    }

    private fun joinFamily(user: User, request: RegisterRequest): Family {
        val code = request.familyCode ?: throw IllegalArgumentException("familyCode is required for join action")
        val relation = request.relation ?: throw IllegalArgumentException("relation is required for join action")

        val family = familyRepository.findByFamilyCode(code) ?: throw IllegalArgumentException("Family code not found")
        familyMemberRepository.save(
            FamilyMember(userId = user.id, familyId = family.id, relation = relation)
        )
        Log.i("User ${user.id} joined family ${family.id} as $relation")
        return family
    }

    private fun generateFamilyCode(length: Int = 8): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..length).map { chars.random() }.joinToString("")
    }
} 