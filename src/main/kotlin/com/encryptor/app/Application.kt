package com.encryptor.app

import com.encryptor.plugins.*
import com.encryptor.security.hashing.SHA256HashingService
import com.encryptor.security.token.JwtTokenService
import com.encryptor.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.resources.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(Resources)

    databaseModule()

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = environment.config.property("jwt.secret").getString()
    )
    val hashingService = SHA256HashingService()

    routingModule(hashingService, tokenService, tokenConfig)
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
}

//fun main() {
//    embeddedServer(Netty, port = SERVER_PORT, host = SERVER_HOST) {
//
//    }.start(wait = true)
//}
