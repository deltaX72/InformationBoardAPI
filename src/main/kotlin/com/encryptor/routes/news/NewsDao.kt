package com.encryptor.routes.news

import com.encryptor.data.models.NewsModel
import com.encryptor.routes.base.BaseDao
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq

class NewsDao(override val table: NewsTable = NewsTable): BaseDao(table) {

    suspend fun selectNews(id: Long) =
        super.select(table.id eq id).singleOrNull() as NewsModel?

    @Suppress("UNCHECKED_CAST")
    suspend fun selectAllNews() =
        super.selectAll() as List<NewsModel>

    suspend fun createNews(newsModel: NewsModel) = with(table) {
        dbQuery {
            insert {
                it[username] = newsModel.username
                it[topic] = newsModel.topic
                it[message] = newsModel.message
                it[dateCreated] = newsModel.dateCreated
            }
                .resultedValues
                ?.singleOrNull()
                ?.let(NewsTable::serialize)
        }
    }

    suspend fun deleteNews(id: Long) =
        super.delete(table.id eq id)

    suspend fun deleteAllNews() =
        super.delete(table.id greaterEq 0)
}