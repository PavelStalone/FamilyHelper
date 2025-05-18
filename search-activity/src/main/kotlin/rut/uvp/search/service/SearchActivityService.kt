package rut.uvp.search.service

import org.springframework.stereotype.Service
import rut.uvp.auth.service.UserService
import rut.uvp.calendar.service.CalendarService
import rut.uvp.family.domain.model.Relationship
import rut.uvp.family.service.FamilyService
import rut.uvp.search.model.FamilyMemberSearch

interface SearchActivityService {

    fun findActivity(
        city: String?,
        context: String,
        familyId: String,
        endDate: String? = null,
        startDate: String? = null,
        preferences: List<String>,
        members: List<FamilyMemberSearch>,
    ): List<String>
}

@Service
internal class SearchActivityServiceImpl(
    private val userService: UserService,
    private val familyService: FamilyService,
    private val calendarService: CalendarService,
) : SearchActivityService {

    override fun findActivity(
        city: String?,
        context: String,
        familyId: String,
        endDate: String?,
        startDate: String?,
        preferences: List<String>,
        members: List<FamilyMemberSearch>
    ): List<String> {
        runCatching {
            val user = userService.getCurrentUser()
            val family = requireNotNull(familyService.findFamilyById(familyId))
            val userFamilyMember = familyService.findMemberByUserId(userId = user.id.toString(), family = family)
            requireNotNull(userFamilyMember)

            val familyMembers = members
                .flatMap { member ->
                    with(member) {
                        val relation = Relationship(
                            levelRelation = levelRelation,
                            levelProximity = levelProximity,
                        )

                        familyService.findMembersByRelationship(
                            family = family,
                            relativeFamilyMember = userFamilyMember,
                            relationship = relation,
                            gender = gender,
                            name = name
                        )
                    }
                }
                .distinctBy { member -> member.id }
                .mapNotNull { member ->
                    runCatching {
                        val familyUser = userService.findById(member.userId.toLong())
                        val calendar = calendarService.getEvents(userId = member.userId, counts = 20)


                    }.getOrNull()
                }

        }

        return emptyList()
    }
}
