package rut.uvp.core.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "families")
internal data class Family(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val familyCode: String = "",
    @Column(nullable = false)
    val creatorId: Long = 0
)
