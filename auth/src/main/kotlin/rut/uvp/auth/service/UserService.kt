package rut.uvp.auth.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
import rut.uvp.auth.util.asDomain
import rut.uvp.core.data.model.user.User
import kotlin.jvm.optionals.getOrNull

interface UserService {

    fun getCurrentUser(): User
    fun findById(userId: Long): User?
}

@Service
internal class UserServiceImpl(
    private val userRepositoryJpa: UserRepositoryJpa
) : UserService {

    override fun getCurrentUser(): User {
        val user = SecurityContextHolder.getContext().authentication.principal as? User
//        val user = userRepositoryJpa.findById(2).getOrNull()?.asDomain()
        requireNotNull(user) { "User is not authorized" }

        return user
    }

    override fun findById(userId: Long): User? {
        return userRepositoryJpa.findById(userId).getOrNull()?.asDomain()
    }
}
