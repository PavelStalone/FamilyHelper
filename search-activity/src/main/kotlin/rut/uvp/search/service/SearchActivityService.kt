package rut.uvp.search.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import rut.uvp.auth.service.UserService
import rut.uvp.calendar.service.CalendarService
import rut.uvp.core.ai.config.ChatClientQualifier
import rut.uvp.core.common.log.Log
import rut.uvp.deepsearch.service.DeepSearchService
import rut.uvp.family.domain.model.Family
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Relationship
import rut.uvp.family.service.FamilyService
import rut.uvp.search.model.FamilyMemberSearch
import rut.uvp.search.model.MemberInfo

interface SearchActivityService {

    suspend fun findActivity(
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
    @Qualifier(ChatClientQualifier.QUERY_GENERATOR_CLIENT)
    private val chatClient: ChatClient,
    private val userService: UserService,
    private val familyService: FamilyService,
    private val calendarService: CalendarService,
    private val deepSearchService: DeepSearchService,
) : SearchActivityService {

    override suspend fun findActivity(
        city: String?,
        context: String,
        familyId: String,
        endDate: String?,
        startDate: String?,
        preferences: List<String>,
        members: List<FamilyMemberSearch>
    ): List<String> = runCatching {
        Log.v("findActivity called for family: $familyId")

        val user = userService.getCurrentUser()
        val family = requireNotNull(familyService.findFamilyById(familyId))
        val userFamilyMember = familyService.findMemberByUserId(userId = user.id.toString(), family = family)
        requireNotNull(userFamilyMember)

        Log.i("User: $userFamilyMember")

        val familyMembers = members.asMembersInfo(
            family = family,
            userFamilyMember = userFamilyMember,
        )

        val query = chatClient
            .prompt(
                """
                        |Информация об членах семьи, которые пойдут на мероприятие -----------------
                        |${familyMembers.map { it.toString().replace("\n", " ; ") }}
                        |--------------------------------
                        |Предпочитаемый город: $city
                        |Желаемая дата начала: $startDate
                        |Желаемая дата окончания: $endDate
                        |Предпочтения: $preferences
                        |
                        |Информация от бота: $context
                    """.trimMargin()
            )
            .call()
            .content()
        requireNotNull(query)

        deepSearchService.deepSearch(query).also {
            Log.i("Find activities (${it.size}): $it")
        }
    }
        .onFailure { throwable ->
            Log.e(throwable, "Failed in findActivity")
        }
        .getOrDefault(emptyList())
        .map { it.toString() }

    private fun List<FamilyMemberSearch>.asMembersInfo(
        family: Family,
        userFamilyMember: FamilyMember,
    ): List<MemberInfo> = flatMap { member ->
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
            ).also {
                Log.v("Find member: $it")
            }
        }
    }
        .plus(userFamilyMember)
        .distinctBy { member -> member.id }
        .mapNotNull { member ->
            runCatching {
                val familyUser = userService.findById(member.userId.toLong())
                val calendarEvents = calendarService.getEvents(userId = member.userId, counts = 20)
                requireNotNull(familyUser)

                MemberInfo(
                    user = familyUser,
                    familyMember = member,
                    calendarEvents = calendarEvents,
                )
            }.onFailure { throwable ->
                Log.e(throwable, "failed find member: $member")
            }.getOrNull()
        }
}
