package com.encryptor.routes.auth

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/authenticate")
class AuthenticateRouting