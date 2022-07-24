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

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

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
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
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
//        get("/login"){
//            val name =call.request.queryParameters["name"]
//            val email =call.request.queryParameters["email"]
//            val password = call.request.queryParameters["password"]
//            if(name!=null && password!=null && email!=null){
//                val user= User(email,hashFunction(password),name)
//                call.respond( jwtService.generateToken(user))
//            }
//
//        }
    }
}
