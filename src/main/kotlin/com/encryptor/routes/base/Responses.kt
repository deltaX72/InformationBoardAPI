package com.encryptor.routes.base

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondJson(
    value: T,
    status: HttpStatusCode
) {
    call.respondText(
        text = Json.encodeToString(value),
        status = status
    )
}

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondJson(
    value: List<T>,
    status: HttpStatusCode
) {
    call.respondText(
        text = Json.encodeToString(value),
        status = status
    )
}

//================================ [ 2** ] ===========================

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondFound(
    value: T
) {
    respondJson(
        value = value,
        status = HttpStatusCode.OK
    )
}

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondFound(
    value: List<T>
) {
    respondJson(
        value = value,
        status = HttpStatusCode.OK
    )
}

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondCreated(
    value: T
) {
    respondJson(
        value = value,
        status = HttpStatusCode.Created
    )
}

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondDeleted(
    value: T
) {
    respondJson(
        value = value,
        status = HttpStatusCode.NoContent
    )
}

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondDeleted(
    value: List<T>
) {
    respondJson(
        value = value,
        status = HttpStatusCode.NoContent
    )
}

suspend inline fun <reified T: BaseDataType> PipelineContext<*, ApplicationCall>.respondUpdated(
    newValue: T
) {
    respondJson(
        value = newValue,
        status = HttpStatusCode.OK
    )
}

//================================ [ 4** ] ===========================

suspend fun <T> PipelineContext<*, ApplicationCall>.respondNotFound(
    table: BaseTable,
    parameter: String,
    value: T,
    msg: String ="${table.unitName}: value with $parameter = '$value' was not found!"
) {
    call.respondText(
        msg,
        status = HttpStatusCode.NotFound
    )
}

suspend fun PipelineContext<*, ApplicationCall>.respondNotFound(
    table: BaseTable,
    parameter: String,
    msg: String = "${table.unitName}: value with parameter $parameter were not found!"
) {
    call.respondText(
        msg,
    )
}

suspend fun <T> PipelineContext<*, ApplicationCall>.respondNotDeleted(
    table: BaseTable,
    parameter: String,
    value: T,
    msg: String = "${table.unitName}: value with $parameter = $value was not deleted!"
) {
    call.respondText(
        msg,
        status = HttpStatusCode.Conflict
    )
}

suspend fun <T> PipelineContext<*, ApplicationCall>.respondExists(
    table: BaseTable,
    parameter: String,
    value: T,
    msg: String = "${table.unitName}: value with $parameter = '$value' already exists!"
) {
    call.respondText(
        msg,
        status = HttpStatusCode.BadRequest
    )
}

suspend fun <T> PipelineContext<*, ApplicationCall>.respondNotUpdated(
    id: Long,
    table: BaseTable,
    parameter: String,
    oldValue: T,
    newValue: T
) {
    call.respondText(
        "An unexpected error occurred. ${table.unitName} (id = $id): " +
                "parameter '$parameter' wasn't changed value " +
                "from '$oldValue' to '$newValue'!",
        status = HttpStatusCode.Conflict
    )
}
