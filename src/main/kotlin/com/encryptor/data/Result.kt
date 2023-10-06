package com.encryptor.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

sealed class Result<T> {
    @Serializable
    class Success<T>(val data: T): Result<T>()

    @Serializable
    class Failure<T>(val message: String): Result<T>()

    @Serializable
    class Loading<T>: Result<T>()
}