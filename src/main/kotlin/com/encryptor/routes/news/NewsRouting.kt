package com.encryptor.routes.news

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/news")
class NewsRouting {
    @Serializable
    @Resource("/{id}")
    class Id(val parent: NewsRouting = NewsRouting(), val id: Long)
}