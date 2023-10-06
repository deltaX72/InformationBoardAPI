package com.encryptor.routes.users

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/users")
class UsersRouting {
    @Serializable
    @Resource("/{id}")
    class Id(val parent: UsersRouting = UsersRouting(), val id: Long) {
        @Serializable
        @Resource("/edit")
        class Edit(val parent: Id) {
            @Serializable
            @Resource("/password")
            class Password(val parent: Edit, val password: String)

            @Serializable
            @Resource("/username")
            class Username(val parent: Edit, val username: String)

            @Serializable
            @Resource("/firstname")
            class FirstName(val parent: Edit, val firstName: String)

            @Serializable
            @Resource("/lastname")
            class LastName(val parent: Edit, val lastName: String)
        }
    }

    @Serializable
    @Resource("/get")
    class Get(val parent: UsersRouting = UsersRouting()) {
        @Serializable
        @Resource("/username")
        class Username(val parent: Get = Get(), val username: String)

        @Serializable
        @Resource("/firstname")
        class FirstName(val parent: Get = Get(), val firstName: String)

        @Serializable
        @Resource("/lastname")
        class LastName(val parent: Get = Get(), val lastName: String)

        @Serializable
        @Resource("/token")
        class Token(val parent: Get = Get())
    }

    @Serializable
    @Resource("/signup")
    class SignUp(val parent: UsersRouting = UsersRouting())

    @Serializable
    @Resource("/signin")
    class SignIn(val parent: UsersRouting = UsersRouting())
}