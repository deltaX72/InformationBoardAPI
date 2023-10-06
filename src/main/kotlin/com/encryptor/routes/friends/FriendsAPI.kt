package com.encryptor.routes.friends

import com.encryptor.data.models.Friends
import com.encryptor.data.models.NewsModel
import com.encryptor.routes.base.respondCreated
import com.encryptor.routes.base.respondExists
import com.encryptor.routes.base.respondFound
import com.encryptor.routes.base.respondNotFound
import com.encryptor.routes.news.NewsDao
import com.encryptor.routes.news.NewsTable
import com.encryptor.routes.users.UsersDao
import com.encryptor.routes.users.UsersTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.patch
import io.ktor.server.response.*

fun Route.createFriend() = post<FriendsRouting> {
    val request = call.receiveOrNull<Friends>()
        ?: return@post call.respond(HttpStatusCode.BadRequest)

    val friend = Friends(
        senderId = request.senderId,
        receiverId = request.receiverId,
        time = System.currentTimeMillis()
    )

    if (friend.senderId == friend.receiverId)
        return@post call.respond(
            status = HttpStatusCode.Conflict,
            message = "You cannot be a friend with yourself!"
        )

    val createdFriend = FriendsDao()
        .createFriend(friend)
        ?: return@post respondExists(FriendsTable, FriendsTable.id.name, request.id,
            msg = "User with id ${request.senderId} already has a friend user with id ${request.receiverId}!")
    respondCreated(createdFriend)
}

fun Route.selectAllFriends() = get<FriendsRouting> {
    if (!call.request.queryParameters.isEmpty())
        return@get respondNotFound(FriendsTable, FriendsTable.id.name, null)
    respondFound(FriendsDao().selectAllFriends())
}

fun Route.selectFriends() = get<FriendsRouting.Id> {
    if (!call.request.queryParameters.isEmpty())
        return@get respondNotFound(FriendsTable, FriendsTable.id.name, null)
    respondFound(FriendsDao().selectFriends(it.id))
}