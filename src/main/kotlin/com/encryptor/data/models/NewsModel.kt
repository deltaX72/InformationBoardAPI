package com.encryptor.data.models

import com.encryptor.routes.base.BaseDataType
import kotlinx.serialization.Serializable

@Serializable
data class NewsModel(
    val id: Long = -1,
    val image: ByteArray? = null,
    val username: String,
    val topic: String,
    val message: String,
    val dateCreated: Long
): BaseDataType {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsModel

        if (id != other.id) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (username != other.username) return false
        if (topic != other.topic) return false
        if (message != other.message) return false
        if (dateCreated != other.dateCreated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + username.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + dateCreated.hashCode()
        return result
    }
}