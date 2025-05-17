package rut.uvp.auth.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret:secret}")
    private val secret: String,
    @Value("\${jwt.expirationMillis:3600000}")
    private val expirationMillis: Long,
) {

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: Long, email: String): String {
        val now = Date()
        val expiry = Date(now.time + expirationMillis)
        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(now)
            .withExpiresAt(expiry)
            .sign(algorithm)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val verifier = JWT.require(algorithm).build()
            verifier.verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractUserId(token: String): Long? = try {
        val verifier = JWT.require(algorithm).build()
        verifier.verify(token).subject.toLong()
    } catch (e: Exception) {
        null
    }
} 