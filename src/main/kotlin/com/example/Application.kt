package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import io.ktor.application.*
import com.example.repository.DatabaseFactory
import com.example.repository.Repo
import com.example.routes.UserRoute
import com.example.routes.noteRoutes
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }
    DatabaseFactory.init()

    install(CallLogging)
    install(ContentNegotiation) {
        gson()
    }

    install(Authentication) {
        /**
         * Inform which authentication we are using
         */
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user//we have to return a Principal, authenticating the User
            }
        }
    }

    install(Locations)


    routing {
        UserRoute(db, jwtService, hashFunction)
        noteRoutes(db)
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
