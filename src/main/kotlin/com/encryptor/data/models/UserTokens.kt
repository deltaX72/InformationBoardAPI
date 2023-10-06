package com.encryptor.data.models

import com.encryptor.routes.base.BaseDataType
import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    val id: Long = -1,
    val userId: Long,
    val token: String,
    val isActive: Boolean = false
): BaseDataType