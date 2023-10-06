package com.encryptor.routes.friends

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/friends")
class FriendsRouting {
    @Serializable
    @Resource("/{id}")
    class Id(val parent: FriendsRouting = FriendsRouting(), val id: Long)
}