package com.encryptor.routes.friends

import com.encryptor.data.models.Friends
import com.encryptor.routes.base.BaseDao
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class FriendsDao(override val table: FriendsTable = FriendsTable): BaseDao(table) {

    suspend fun createFriend(friends: Friends) = with(table) {
        if (!isExists(friends.senderId, friends.receiverId)) {
            dbQuery {
                return@dbQuery insert {
                    it[senderId] = friends.senderId
                    it[receiverId] = friends.receiverId
                    it[time] = friends.time
                }
                    .resultedValues
                    ?.singleOrNull()
                    ?.let(FriendsTable::serialize)
            }
        } else null
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun selectAllFriends() =
        super.selectAll() as List<Friends>

    suspend fun selectFriends(senderId: Long) =
        selectAllFriends().filter {
            it.senderId == senderId || it.receiverId == senderId
        }

    suspend fun selectFriend(senderId: Long, receiverId: Long) =
        selectAllFriends().firstOrNull {
            (it.senderId == senderId && it.receiverId == receiverId) ||
            (it.senderId == receiverId && it.receiverId == senderId)
        }

    suspend fun deleteFriend(senderId: Long, receiverId: Long) = with(table) {
        val friend = selectFriend(senderId, receiverId) ?: return@with false
        return super.delete(table.id eq friend.id)
    }

    suspend fun isExists(senderId: Long, receiverId: Long) = with(table) {
        selectFriend(senderId, receiverId) != null
    }
}