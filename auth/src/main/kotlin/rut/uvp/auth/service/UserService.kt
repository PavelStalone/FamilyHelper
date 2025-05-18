package rut.uvp.auth.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import rut.uvp.core.data.model.user.User

interface UserService {

    fun getCurrentUser(): User
}

@Service
internal class UserServiceImpl : UserService {

    override fun getCurrentUser(): User {
        val user = SecurityContextHolder.getContext().authentication.principal as? User
        requireNotNull(user) { "User is not authorized" }

        return user
    }
}
