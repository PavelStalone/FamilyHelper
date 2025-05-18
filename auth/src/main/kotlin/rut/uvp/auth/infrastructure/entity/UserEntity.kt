package rut.uvp.auth.infrastructure.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
internal data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val email: String = "",
    @Column(nullable = false)
    val passwordHash: String = "",
    @Column(nullable = false)
    val name: String = ""
)
