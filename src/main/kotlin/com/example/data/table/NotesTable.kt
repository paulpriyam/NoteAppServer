package com.example.data.table

import org.jetbrains.exposed.sql.Table

object NotesTable : Table() {
    val noteId = varchar("id", 512)
    val noteTitle = varchar("title", 512)
    val noteDescription = varchar("description", 512)
    val date = long("date")
    val userEmail = varchar("email", 512).references(UserTable.email)

    override val primaryKey: PrimaryKey = PrimaryKey(noteId)
}