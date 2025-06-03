package rut.uvp.auth.service

import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import rut.uvp.auth.infrastructure.repository.UserRepositoryJpa
import rut.uvp.auth.util.asDomain
import rut.uvp.core.data.model.user.User
import kotlin.jvm.optionals.getOrNull

interface UserService {

    fun getCurrentUser(): User
    fun getCurrentUserReactive(): Mono<User>
    fun findById(userId: Long): User?
}

@Service
internal class UserServiceImpl(
    private val userRepositoryJpa: UserRepositoryJpa
) : UserService {

    override fun getCurrentUser(): User {
        // For non-reactive contexts, use SecurityContextHolder
        val principal = SecurityContextHolder.getContext().authentication?.principal as? User
        
        // If not available, use a default user for testing (you can remove this in production)
        return principal ?: userRepositoryJpa.findById(2).getOrNull()?.asDomain()
            ?: throw IllegalStateException("User is not authorized")
    }
    
    override fun getCurrentUserReactive(): Mono<User> {
        // For reactive contexts, use ReactiveSecurityContextHolder
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication.principal as? User }
            .switchIfEmpty(
                // If not available in reactive context, try to get from a blocking repository on a separate thread
                Mono.fromCallable { 
                    userRepositoryJpa.findById(2).getOrNull()?.asDomain() 
                }.subscribeOn(Schedulers.boundedElastic())
            )
            .filter { it != null }
            .cast(User::class.java)
            .switchIfEmpty(Mono.error(IllegalStateException("User is not authorized")))
    }

    override fun findById(userId: Long): User? {
        return userRepositoryJpa.findById(userId).getOrNull()?.asDomain()
    }
}
