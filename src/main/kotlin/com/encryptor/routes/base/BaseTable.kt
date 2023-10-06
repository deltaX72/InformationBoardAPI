package com.encryptor.routes.base

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class BaseTable: Table() {
    open val unitName: String = "Base"

    abstract fun serialize(row: ResultRow): BaseDataType

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}