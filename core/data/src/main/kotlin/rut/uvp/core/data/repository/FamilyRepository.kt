package rut.uvp.core.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rut.uvp.core.data.entity.Family

@Repository
interface FamilyRepository : JpaRepository<Family, Long> {
    fun findByFamilyCode(familyCode: String): Family?
    fun existsByFamilyCode(familyCode: String): Boolean
} 