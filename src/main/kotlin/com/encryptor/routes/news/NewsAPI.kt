package com.encryptor.routes.news

import com.encryptor.data.models.NewsModel
import com.encryptor.routes.base.*
import com.encryptor.routes.users.UsersDao
import com.encryptor.routes.users.UsersTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.delete
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getNews() = get<NewsRouting> {
    if (!call.request.queryParameters.isEmpty())
        return@get respondNotFound(NewsTable, NewsTable.id.name, null)
    respondFound(NewsDao().selectAllNews())
}

fun Route.deleteNews() = delete<NewsRouting.Id> {
    val news = NewsDao()
        .selectNews(it.id)
        ?: return@delete respondNotFound(NewsTable, NewsTable.id.name, it.id)
    NewsDao()
        .deleteNews(news.id)
        .takeIf { it }
        ?: return@delete respondNotDeleted(NewsTable, NewsTable.id.name, it.id)
    respondDeleted(news)
}

fun Route.createNews() = post<NewsRouting> {
    val request = call.receiveOrNull<NewsModel>()
        ?: return@post call.respond(HttpStatusCode.BadRequest)

    val newsModel = NewsModel(
        username = request.username,
        topic = request.topic,
        message = request.message,
        dateCreated = System.currentTimeMillis()
    )

    val insertedNews = NewsDao()
        .createNews(newsModel)
        ?: return@post respondExists(NewsTable, NewsTable.username.name, request.username)
    respondCreated(insertedNews)
}