package rut.uvp.auth.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
import rut.uvp.auth.util.asDomain
import rut.uvp.core.data.model.user.User

interface UserService {

    fun getCurrentUser(): User
}

@Service
internal class UserServiceImpl(
    private val userRepository: UserRepositoryJpa,
) : UserService {

    override fun getCurrentUser(): User {
        val userId = SecurityContextHolder.getContext().authentication.principal as? Long
        requireNotNull(userId) { "User is not authorized" }

        return userRepository.findById(userId).get().asDomain()
    }
}
