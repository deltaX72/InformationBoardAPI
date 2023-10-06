package com.encryptor.data.models

import com.encryptor.routes.base.BaseDataType
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = -1,
    val username: String,
    val password: String,
    val salt: String,
    val dateCreated: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val token: String? = null
): BaseDataType