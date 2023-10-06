package com.encryptor.routes.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authenticate() {
    authenticate {
        get<AuthenticateRouting> {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInformation() = authenticate {
    get<SecretRouting> {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        val username = principal?.getClaim("username", String::class)
        call.respond(HttpStatusCode.OK, "Your userId = $userId, username = '$username'")
    }
}