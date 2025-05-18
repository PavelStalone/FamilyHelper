package rut.uvp.auth.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rut.uvp.auth.infrastructure.entity.UserEntity

@Repository
internal interface UserRepositoryJpa : JpaRepository<UserEntity, Long> {

    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserEntity?
}
