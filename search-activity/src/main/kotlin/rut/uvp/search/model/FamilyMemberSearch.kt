package rut.uvp.search.model

import org.springframework.ai.tool.annotation.ToolParam
import rut.uvp.family.domain.model.Gender

data class FamilyMemberSearch(
    @ToolParam(description = "Пол члена семьи", required = true)
    val gender: Gender,
    @ToolParam(
        description = "Уровень родства. Аналогия поколения - Отец или Мама это -1 (Старшее поколение), Бабушка или дедушка это -2 (Еще более старшее поколение) и т.д. Брат, Сестра или жена это 0 поколение. Сын или дочь это 1 поколение и т.д.",
        required = true
    )
    val levelRelation: Int,
    @ToolParam(
        description = "Степень близости. Родные это 0 степень близости. Двоюродные родственики это 1 степень, троюродные 2 и т.д.",
        required = true
    )
    val levelProximity: Int,
    @ToolParam(description = "Имя члена семьи", required = false)
    val name: String? = null,
)
