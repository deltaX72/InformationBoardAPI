package com.encryptor.routes.users

import com.encryptor.routes.base.*
import com.encryptor.data.requests.AuthRequest
import com.encryptor.data.models.User
import com.encryptor.data.responses.AuthResponse
import com.encryptor.routes.auth.AuthenticateRouting
import com.encryptor.security.hashing.HashingService
import com.encryptor.security.hashing.SaltedHash
import com.encryptor.security.token.TokenClaim
import com.encryptor.security.token.TokenConfig
import com.encryptor.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.patch
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.getUsers() = authenticate {
    get<UsersRouting> {
        if (!call.request.queryParameters.isEmpty())
            return@get respondNotFound(UsersTable, UsersTable.id.name, null)
        respondFound(UsersDao().selectAllUsers())
    }
}

fun Route.getUser() = authenticate {
    get<UsersRouting.Id> {
        val user = UsersDao()
            .selectUser(it.id)
            .takeIf { it != null }
            ?: return@get respondNotFound(UsersTable, UsersTable.id.name, it.id)
        respondFound(user)
    }
}

fun Route.getUserByUsername() = authenticate {
    get<UsersRouting.Get.Username> {
        val user = UsersDao()
            .selectUser(it.username)
            ?: return@get respondNotFound(UsersTable, UsersTable.username.name, it.username)
        respondFound(user)
    }
}

fun Route.getUserByToken() = authenticate {
    get<UsersRouting.Get.Token> {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class).toString().toLong()
        val username = principal?.getClaim("username", String::class).toString()
        println(userId)
        println(username)
//        println(it.token)
        val user = UsersDao()
//            .selectUser(username = username)
            .selectUser(userId = userId)
            ?: return@get respondNotFound(UsersTable, UsersTable.id.name)
        println("TRUEEEEEEEE")
        respondFound(user)
    }
}

fun Route.signUpUser(
    hashingService: HashingService
) = post<UsersRouting.SignUp> {
    val request = call.receiveOrNull<AuthRequest>() ?:
        return@post call.respond(HttpStatusCode.BadRequest)

    val areFieldsBlank = request.username.isBlank() || request.password.isBlank()

    if (areFieldsBlank)
        return@post call.respond(
            status = HttpStatusCode.Conflict,
            message = "All fields must be filled!"
        )
    if (request.password.length < 8)
        return@post call.respond(
            status = HttpStatusCode.Conflict,
            message = "Password is too short. It must contains 8 and more symbols!"
        )

    val saltedHash = hashingService.generateSaltedHash(request.password)
    val user = User(
        username = request.username,
        password = saltedHash.hash,
        salt = saltedHash.salt,
        dateCreated = System.currentTimeMillis()
    )

    val insertedUser = UsersDao()
        .insertUser(user)
        ?: return@post respondExists(UsersTable, UsersTable.username.name, request.username)
    respondCreated(insertedUser)
}

fun Route.signInUser(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) = post<UsersRouting.SignIn> {
    val request = call.receiveOrNull<AuthRequest>() ?:
        return@post call.respond(HttpStatusCode.BadRequest)
    val user = UsersDao()
        .selectUser(request.username)
        .takeIf { it != null }
        ?: return@post respondNotFound(UsersTable, UsersTable.username.name, request.username)

    val isValidPassword = hashingService.verify(
        value = request.password,
        saltedHash = SaltedHash(
            hash = user.password,
            salt = user.salt
        )
    )
    println(isValidPassword)
    println(request.password)
    println(user.password)
    println(user.username)
    if (!isValidPassword)
        return@post call.respond(HttpStatusCode.Conflict, "Incorrect password!")
    val token = tokenService.generate(
        config = tokenConfig,
        TokenClaim(
            name = "userId",
            value = user.id.toString()
        ),
        TokenClaim(
            name = "username",
            value = user.username
        )
    )
    println(token)

    UsersDao()
        .update(UsersTable.id eq user.id, UsersTable.token, token)

    call.respond(
        status = HttpStatusCode.OK,
        message = AuthResponse(
            token = token
        )
    )
}

fun Route.deleteUser() = authenticate {
    delete<UsersRouting.Id> {
        val user = UsersDao()
            .selectUser(it.id)
            ?: return@delete respondNotFound(UsersTable, UsersTable.id.name, it.id)
        UsersDao()
            .deleteUser(user.id)
            .takeIf { it }
            ?: return@delete respondNotDeleted(UsersTable, UsersTable.id.name, it.id)
        respondDeleted(user)
    }
}

fun Route.deleteAllUsers() = authenticate {
    delete<UsersRouting> {
        val users = UsersDao()
            .selectAllUsers()
        UsersDao()
            .deleteAllUsers()
            .takeIf { it }
            ?: return@delete respondNotFound(UsersTable, UsersTable.id.name)
        respondDeleted(users)
    }
}

fun Route.updateUserPassword() = authenticate {
    patch<UsersRouting.Id.Edit.Password> {
        val id = it.parent.parent.id
        val password = it.password

        val user = UsersDao()
            .selectUser(id)
            .takeIf { it != null }
            ?: return@patch respondNotFound(UsersTable, UsersTable.id.name, id)

        UsersDao()
            .changeUserPassword(id, password)
            .takeIf { it }
            ?: return@patch respondNotUpdated(
                id = id,
                table = UsersTable,
                parameter = UsersTable.password.name,
                oldValue = UsersDao().getField(id, UsersTable.password),
                newValue = password
            )
        respondUpdated(UsersDao().selectUser(user.id)!!)
    }
}

fun Route.updateUserUsername() = authenticate {
    patch<UsersRouting.Id.Edit.Username> {
        val id = it.parent.parent.id
        val username = it.username

        val user = UsersDao()
            .selectUser(id)
            .takeIf { it != null }
            ?: return@patch respondNotFound(UsersTable, UsersTable.id.name, id)

        UsersDao()
            .changeUserUsername(user.id, username)
            .takeIf { it }
            ?: return@patch respondNotUpdated(
                id = id,
                table = UsersTable,
                parameter = UsersTable.username.name,
                oldValue = user.username,
                newValue = username
            )
        respondUpdated(UsersDao().selectUser(user.id)!!)
    }
}

fun Route.updateUserFirstName() = authenticate {
    patch<UsersRouting.Id.Edit.FirstName> {
        val id = it.parent.parent.id
        val firstName = it.firstName

        val user = UsersDao()
            .selectUser(id)
            .takeIf { it != null }
            ?: return@patch respondNotFound(UsersTable, UsersTable.id.name, id)

        UsersDao()
            .changeUserFirstName(user.id, firstName)
            .takeIf { it }
            ?: return@patch respondNotUpdated(
                id = id,
                table = UsersTable,
                parameter = UsersTable.firstName.name,
                oldValue = user.firstName,
                newValue = firstName
            )
        respondUpdated(UsersDao().selectUser(user.id)!!)
    }
}

fun Route.updateUserLastName() = authenticate {
    patch<UsersRouting.Id.Edit.LastName> {
        val id = it.parent.parent.id
        val lastName = it.lastName

        val user = UsersDao()
            .selectUser(id)
            .takeIf { it != null }
            ?: return@patch respondNotFound(UsersTable, UsersTable.id.name, id)

        UsersDao()
            .changeUserLastName(user.id, lastName)
            .takeIf { it }
            ?: return@patch respondNotUpdated(
                id = id,
                table = UsersTable,
                parameter = UsersTable.lastName.name,
                oldValue = user.lastName,
                newValue = lastName
            )
        respondUpdated(UsersDao().selectUser(user.id)!!)
    }
}