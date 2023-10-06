package com.encryptor.app

import com.encryptor.routes.auth.authenticate
import com.encryptor.routes.auth.getSecretInformation
import com.encryptor.routes.friends.FriendsTable
import com.encryptor.routes.friends.createFriend
import com.encryptor.routes.friends.selectAllFriends
import com.encryptor.routes.friends.selectFriends
import com.encryptor.routes.news.NewsTable
import com.encryptor.routes.news.createNews
import com.encryptor.routes.news.deleteNews
import com.encryptor.routes.news.getNews
import com.encryptor.routes.users.UsersTable
import com.encryptor.routes.users.*
import com.encryptor.security.hashing.HashingService
import com.encryptor.security.token.TokenConfig
import com.encryptor.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.routingModule(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {

        // Users

        getUsers()
        getUser()
        getUserByUsername()
        getUserByToken()

        signUpUser(hashingService)
        signInUser(hashingService, tokenService, tokenConfig)

        authenticate()
        getSecretInformation()

        deleteUser()
        deleteAllUsers()

        updateUserUsername()
        updateUserPassword()
        updateUserFirstName()
        updateUserLastName()

        // News

        getNews()
        createNews()
        deleteNews()

        // Friends

        createFriend()
        selectAllFriends()
        selectFriends()
    }
}

fun Application.databaseModule() {
    val driver = environment.config.property("database.driver").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()
    val host = environment.config.property("database.host").getString()
    val port = environment.config.property("database.port").getString()
    val name = environment.config.property("database.name").getString()

    transaction(Database.connect(
        url = "jdbc:postgresql://$host:$port/$name",
        driver = driver,
        user = user,
        password = password
    )) {
//        SchemaUtils.create(Users)
        SchemaUtils.createMissingTablesAndColumns(
            UsersTable,
            NewsTable,
            FriendsTable
        )

        SchemaUtils.createStatements(
            UsersTable,
            NewsTable,
            FriendsTable
        )
    }
}