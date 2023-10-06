package com.encryptor.routes.users

import com.encryptor.routes.base.BaseDao
import com.encryptor.data.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq

class UsersDao(override val table: UsersTable = UsersTable): BaseDao(table) {
    suspend fun selectUser(userId: Long) =
        super.select(table.id eq userId).singleOrNull() as User?

    suspend fun selectUser(username: String) =
        super.select(table.username eq username).singleOrNull() as User?

    suspend fun selectUserByToken(token: String) =
        super.select(table.token eq token).singleOrNull() as User?

    @Suppress("UNCHECKED_CAST")
    suspend fun selectAllUsers() =
        super.selectAll() as List<User>

    suspend fun isUserExists(userId: Long) =
        super.isExists(table.id eq userId)

    suspend fun isUserExists(username: String) =
        super.isExists(table.username eq username)

    suspend fun insertUser(
        user: User
    ) = with(table) {
        dbQuery {
            if (isUserExists(user.username))
                return@dbQuery null
            insert {
                it[username] = user.username
                it[password] = user.password
                it[salt] = user.salt
                it[dateCreated] = user.dateCreated
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[token] = user.token
            }
                .resultedValues
                ?.singleOrNull()
                ?.let(::serialize)
        }
    }

    suspend fun deleteUser(userId: Long) =
        super.delete(table.id eq userId)

    suspend fun deleteUser(username: String) =
        super.delete(table.username eq username)

    suspend fun deleteAllUsers() =
        super.delete(table.id greaterEq 0)

    suspend fun <T: Any> getField(userId: Long, column: Column<T>) =
        super.getField(table.id eq userId, column).singleOrNull()

    suspend fun changeUserPassword(
        userId: Long,
        newPassword: String
    ) =
        selectUser(userId)
            .let {
                super.update(table.id eq userId, table.password, newPassword) > 0
            }

    suspend fun changeUserUsername(
        userId: Long,
        newUsername: String
    ) =
        selectUser(userId)
            .let {
                super.update(table.id eq userId, table.username, newUsername) > 0
            }

    suspend fun changeUserFirstName(
        userId: Long,
        firstName: String
    ) =
        selectUser(userId)
            .let {
                super.update(table.id eq userId, table.firstName, firstName) > 0
            }

    suspend fun changeUserLastName(
        userId: Long,
        lastName: String
    ) =
        selectUser(userId)
            .let {
                super.update(table.id eq userId, table.lastName, lastName) > 0
            }
}