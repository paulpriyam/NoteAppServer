package com.example.repository

import com.example.data.model.Notes
import com.example.data.model.User
import com.example.data.table.NotesTable
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class Repo {

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[name] = user.name
                ut[hashPassword] = user.hashPassword
                ut[email] = user.email
            }
        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map {
                rowToUser(it)
            }
            .firstOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) return null

        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            name = row[UserTable.name]
        )
    }

    suspend fun addNote(note: Notes, email: String) {
        dbQuery {
            NotesTable.insert { nt ->
                nt[noteId] = note.id
                nt[noteDescription] = note.description
                nt[noteTitle] = note.title
                nt[userEmail] = email
                nt[date] = note.date
            }
        }
    }

    suspend fun updateNote(note: Notes, email: String) {
        dbQuery {
            NotesTable.update(
                where = { NotesTable.userEmail.eq(email) and NotesTable.noteId.eq(note.id) }
            ) { nt ->
                nt[noteDescription] = note.description
                nt[noteTitle] = note.title
                nt[date] = note.date
            }
        }
    }

    suspend fun deleteNote(noteId: String, email: String) {
        dbQuery {
            NotesTable.deleteWhere { NotesTable.userEmail.eq(email) and NotesTable.noteId.eq(noteId) }
        }
    }

    suspend fun getAllNotes(email: String): List<Notes> = dbQuery {
        NotesTable.select {
            NotesTable.userEmail.eq(email)
        }.mapNotNull {
            rowToNote(it)
        }
    }

    private fun rowToNote(row: ResultRow): Notes {
        return Notes(
            id = row[NotesTable.noteId],
            title = row[NotesTable.noteTitle],
            description = row[NotesTable.noteDescription],
            date = row[NotesTable.date]
        )
    }
}