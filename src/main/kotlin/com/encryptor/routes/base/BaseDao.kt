package com.encryptor.routes.base

import org.jetbrains.exposed.sql.*

abstract class BaseDao(open val table: BaseTable) {
    suspend fun select(
        condition: Op<Boolean>,
    ) = with(table) {
        dbQuery {
            select { condition }
                .map(::serialize)
        }
    }

    suspend fun selectAll() = with(table) {
        dbQuery {
            selectAll().map(::serialize)
        }
    }

    suspend fun <T> update(
        condition: Op<Boolean>,
        column: Column<T>,
        value: T
    ) = with(table) {
        dbQuery {
            update({ condition }) {
                it[column] = value
            }
        }
    }

    suspend fun delete(
        condition: Op<Boolean>
    ) = with(table) {
        dbQuery {
            deleteWhere { condition } > 0
        }
    }

    suspend fun isExists(
        condition: Op<Boolean>
    ) = with(table) {
        dbQuery {
            this@BaseDao.select(condition).isNotEmpty()
        }
    }

    suspend fun isNotExists(
        condition: Op<Boolean>
    ) = !this.isExists(condition)

    suspend fun <T: Any> getField(
        condition: Op<Boolean>,
        column: Column<T>,
    ) = with(table) {
        dbQuery {
            select { condition }
            .map {
                it[column]
            }
        }
    }
}