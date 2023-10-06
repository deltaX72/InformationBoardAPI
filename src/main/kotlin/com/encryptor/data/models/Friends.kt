package com.encryptor.data.models

import com.encryptor.routes.base.BaseDataType
import kotlinx.serialization.Serializable

@Serializable
data class Friends(
    val id: Long = -1,
    val senderId: Long,
    val receiverId: Long,
    val time: Long = -1
): BaseDataType