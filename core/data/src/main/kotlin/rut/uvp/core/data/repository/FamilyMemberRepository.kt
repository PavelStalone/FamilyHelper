package rut.uvp.core.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rut.uvp.core.data.entity.FamilyMember

@Repository
interface FamilyMemberRepository : JpaRepository<FamilyMember, Long> {
    fun findByFamilyIdAndUserId(familyId: Long, userId: Long): FamilyMember?
    fun findFirstByUserId(userId: Long): FamilyMember?
} 