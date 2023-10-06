package com.encryptor.routes.users

import com.encryptor.routes.base.BaseTable
import com.encryptor.data.models.User
import org.jetbrains.exposed.sql.*

object UsersTable : BaseTable() {
    val id = long("id").autoIncrement().uniqueIndex()

    val username = varchar("username", 20).uniqueIndex()
    val password = varchar("password", 64)
    val salt = varchar("salt", 64)
    val dateCreated = long("date_created")
    val firstName = varchar("first_name", 40).nullable()
    val lastName = varchar("last_name", 40).nullable()
    val token = varchar("token", 1000).nullable()

    override val primaryKey = PrimaryKey(id, name = "pk_users_id")

    override val unitName: String = "User"

    override fun serialize(row: ResultRow) = User(
        id = row[id],
        username = row[username],
        password = row[password],
        salt = row[salt],
        dateCreated = row[dateCreated],
        firstName = row[firstName],
        lastName = row[lastName],
        token = row[token]
    )
}