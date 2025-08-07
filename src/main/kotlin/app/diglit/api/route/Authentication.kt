/*
 * DigLit - Digital Literacy License
 * Copyright (C) 2025 Nhan Huynh
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package app.diglit.api.route

import app.diglit.api.authentication.JwtConfig
import app.diglit.api.dto.ChangeEmailRequest
import app.diglit.api.dto.ChangePasswordRequest
import app.diglit.api.dto.LoginRequest
import app.diglit.api.dto.LoginResponse
import app.diglit.api.dto.RegisterRequest
import app.diglit.api.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

/**
 * Defines public authentication routes for user registration and login.
 *
 * These endpoints are accessible without prior authentication:
 * - `POST /register`: Creates a new user account.
 * - `POST /login`: Authenticates a user and returns a JWT token.
 *
 * @param config The [JwtConfig] used to generate signed JWT tokens for authenticated users.
 */
fun Route.publicAuthRoutes(config: JwtConfig) {
    post("/register") {
        val request = call.receive<RegisterRequest>()
        val user =
            UserRepository.create(
                email = request.email.trim(),
                firstName = request.firstName.trim(),
                lastName = request.lastName.trim(),
                password = request.password,
            )
        call.respond(HttpStatusCode.Created, user)
    }

    post("/login") {
        val request = call.receive<LoginRequest>()
        val user = UserRepository.authenticate(request.email.trim(), request.password)
        val token = config.generateEmailToken(user.email)
        call.respond(HttpStatusCode.OK, LoginResponse(token))
    }
}

/**
 * Returns the authenticated user's email from the JWT token.
 * Responds with 401 if the token is missing or invalid.
 */
suspend fun ApplicationCall.getAuthenticatedEmail(): String? {
    val principal = principal<JWTPrincipal>()
    val email = principal?.payload?.getClaim("email")?.asString()

    return if (!email.isNullOrBlank()) {
        email
    } else {
        respond(HttpStatusCode.Unauthorized, "Missing or invalid token")
        null
    }
}

fun Route.privateAuthRoutes(config: JwtConfig) {
    get("/me") {
        val email = call.getAuthenticatedEmail() ?: return@get
        val user = UserRepository.findByEmail(email)
        call.respond(HttpStatusCode.OK, user)
    }

    post("/change-password") {
        val email = call.getAuthenticatedEmail() ?: return@post
        val request = call.receive<ChangePasswordRequest>()
        val user = UserRepository.changePassword(email, request.oldPassword, request.newPassword)
        call.respond(HttpStatusCode.OK, user)
    }

    post("/change-email") {
        val oldEmail = call.getAuthenticatedEmail() ?: return@post
        val request = call.receive<ChangeEmailRequest>()
        val updatedUser = UserRepository.changeEmail(oldEmail, request.newEmail.trim(), request.password)
        val newToken = config.generateEmailToken(updatedUser.email)
        call.respond(HttpStatusCode.OK, LoginResponse(newToken))
    }

    delete {
        val email = call.getAuthenticatedEmail() ?: return@delete
        val deletedUser = UserRepository.deleteByEmail(email)
        call.respond(HttpStatusCode.OK, deletedUser)
    }
}
