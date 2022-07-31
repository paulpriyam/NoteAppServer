package com.example.routes

import com.example.authentication.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.StandardResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

@Location(REGISTER_REQUEST)
class UserRegisterRoute

@Location(LOGIN_REQUEST)
class UserLoginRoute

fun Route.UserRoute(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserRegisterRoute> {
        val request = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, StandardResponse(false, "Field is missing"))
            return@post
        }

        try {
            val user = User(request.email, hashFunction(request.password), request.name)
            db.addUser(user)
            call.respond(HttpStatusCode.OK, StandardResponse(true, jwtService.generateToken(user)))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, StandardResponse(false, e.message ?: "Some Error Occurred"))
        }
    }

    post<UserLoginRoute> {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, StandardResponse(false, "Field is missing"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, StandardResponse(false, "entered wrong email"))
            } else {
                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, StandardResponse(true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, StandardResponse(false, "Entered wrong password"))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, e.message ?: "Some Error Occurred")
        }
    }
}