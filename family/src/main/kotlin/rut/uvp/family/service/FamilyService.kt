package rut.uvp.family.service

import org.springframework.stereotype.Service

interface FamilyService {

    fun addMember()
    fun createFamily()
    fun getMemberByRole()
    fun findFamilyByCode()
}

@Service
internal class FamilyServiceImpl(

) {

}
