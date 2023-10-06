package com.encryptor.routes.friends

import com.encryptor.data.models.Friends
import com.encryptor.routes.base.BaseTable
import com.encryptor.routes.users.UsersTable
import com.encryptor.routes.users.UsersTable.references
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

object FriendsTable: BaseTable() {
    val id = long("id").autoIncrement()
//    val senderId = long("sender_id")
//        .references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
//        .uniqueIndex()
//    val receiverId = long("receiver_id")
//        .references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
//        .uniqueIndex()

    val senderId = reference("sender_id", UsersTable.id)
    val receiverId = reference("receiver_id", UsersTable.id)
    val time = long("time")

    override val primaryKey: PrimaryKey = PrimaryKey(id, name = "pk_friends_id")

    override val unitName: String = "Friends"

    init {
        uniqueIndex("unique_friends_idx", senderId, receiverId)
    }

    override fun serialize(row: ResultRow) = Friends(
        senderId = row[senderId],
        receiverId = row[receiverId],
        time = row[time]
    )

}