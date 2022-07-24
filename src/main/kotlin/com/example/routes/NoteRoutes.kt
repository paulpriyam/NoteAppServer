package com.example.routes

import com.example.data.model.Notes
import com.example.data.model.StandardResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTE = "$NOTES/create"
const val UPDATE_NOTE = "$NOTES/update"
const val DELETE_NOTE = "$NOTES/delete"

fun Route.noteRoutes(
    db: Repo) {

    authenticate("jwt") {
        post(CREATE_NOTE) {
            val note = try {
                call.receive<Notes>()

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, StandardResponse(false, "Missing parameter"))
                return@post
            }

            try {
                val email = call.principal<User>()?.email!!
                    db.addNote(note, email)
                    call.respond(HttpStatusCode.OK, StandardResponse(true, "Note successfully added"))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StandardResponse(false, e.message ?: "Some Error Occurred"))
            }
        }

        get(NOTES) {
            try {
                val email = call.principal<User>()?.email
                email?.let {
                    db.getAllNotes(email)
                    call.respond(HttpStatusCode.OK, StandardResponse(true, "Fetched all notes"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StandardResponse(false, e.message ?: "Some Error Occurred"))
            }
        }

        put(UPDATE_NOTE) {
            val note = try {
                call.receive<Notes>()

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, StandardResponse(false, "Missing parameter"))
                return@put
            }

            try {
                val email = call.principal<User>()?.email
                email?.let {
                    db.updateNote(note, email)
                    call.respond(HttpStatusCode.OK, StandardResponse(true, "Note successfully updated"))
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StandardResponse(false, e.message ?: "Some Error Occurred"))
            }
        }

        delete(DELETE_NOTE) {
            val noteId = try {
                call.request.queryParameters["id"].orEmpty()

            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StandardResponse(false, e.message ?: "Note Id not present"))
                return@delete
            }

            try {
                val email = call.principal<User>()?.email
                email?.let {
                    db.deleteNote(noteId, email)
                    call.respond(HttpStatusCode.OK, StandardResponse(true, "Note deleted"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StandardResponse(false, e.message ?: "Some Error occurred"))
            }
        }
    }
}