package com.example

import com.example.authentication.JwtService
import com.example.authentication.hash
import com.example.data.model.User
import io.ktor.application.*
import com.example.plugins.*
import com.example.repository.DatabaseFactory
import com.example.repository.Repo
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val db =Repo()
    val jwtService=JwtService()
    val hashFunction ={s:String-> hash(s)}
    DatabaseFactory.init()

    install(ContentNegotiation) {
        gson()
    }

    install(Authentication){

    }


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/login"){
            val name =call.request.queryParameters["name"]
            val email =call.request.queryParameters["email"]
            val password = call.request.queryParameters["password"]
            if(name!=null && password!=null && email!=null){
                val user= User(email,hashFunction(password),name)
                call.respond( jwtService.generateToken(user))
            }

        }
    }
}
