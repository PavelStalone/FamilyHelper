package rut.uvp.auth.util

import rut.uvp.auth.infrastructure.entity.UserEntity
import rut.uvp.core.data.model.user.User

internal fun UserEntity.asDomain(): User = User(
    id = id,
    name = name,
    email = email,
)
