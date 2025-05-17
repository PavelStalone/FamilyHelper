package rut.uvp.family.domain.model

/**
 * @see
 * <img width="600" height="400" src="https://u2.9111s.ru/uploads/202408/04/0eacf5f688c3ab85fa3c0d09b14bbebb.jpg"/>
 */
data class Relationship(
    val levelRelation: Int, // Уровень родства (сын, отец и т.д.) y
    val levelProximity: Int, // Степень близости (я, брат, двоюродный брат и т.д.) x
) {

    init {
        require(levelProximity >= 0) { "Level of proximity must be greater or equals 0" }
    }
}
