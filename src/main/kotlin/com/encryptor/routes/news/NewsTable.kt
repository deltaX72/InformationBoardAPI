package com.encryptor.routes.news

import com.encryptor.data.models.NewsModel
import com.encryptor.routes.base.BaseTable
import org.jetbrains.exposed.sql.*

object NewsTable: BaseTable() {
    val id = long("id").autoIncrement()

    val username = varchar("username", 20)
    val topic = varchar("topic", 40)
    val message = varchar("message", 200)
    val dateCreated = long("date_created")

    override val unitName: String = "News"

    override fun serialize(row: ResultRow) = NewsModel(
        id = row[id],
        username = row[username],
        topic = row[topic],
        message = row[message],
        dateCreated = row[dateCreated]
    )
}