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
package app.diglit.api

import app.diglit.api.authentication.JwtConfig
import app.diglit.api.database.DatabaseProviderFactory
import app.diglit.api.repository.ApiException
import app.diglit.api.route.privateAuthRoutes
import app.diglit.api.route.publicAuthRoutes
import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

/**
 * Configures and starts the Ktor server application module.
 *
 * This function performs the following setup steps:
 *
 * 1. **Database Connection**: Initializes and connects to the database using a provider.
 *    During development, it drops and recreates the `Users` table for a clean slate.
 *
 * 2. **Serialization**: Installs [ContentNegotiation] with `kotlinx.serialization` JSON support
 *    for request and response payloads.
 *
 * 3. **Error Handling**: Installs [StatusPages] to globally catch and respond with [ApiException]s.
 *
 * 4. **JWT Authentication**:
 *    - Loads secret and token lifetime from environment variables using `dotenv`.
 *    - Installs [Authentication] with a JWT strategy (`auth-jwt`), validating tokens
 *      based on email claims and configured issuer.
 *
 * 5. **Routing**:
 *    - Public routes (`/auth`) for registration and login.
 *    - Protected routes (`/user`) requiring a valid JWT token.
 *
 * @receiver The [Application] instance to configure.
 */
fun Application.module() {
    // Connect to the database
    val database = DatabaseProviderFactory.create()
    database.connect()

    // Install the ContentNegotiation plugin to handle JSON serialization
    install(ContentNegotiation) {
        json()
    }

    // Install the StatusPages plugin to handle exceptions globally
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(cause.code, cause)
        }
    }

    // Authentication setup
    val environments = dotenv()
    val config =
        JwtConfig(
            secret = environments[JwtConfig.ENVIRONMENT_JWT_SECRET],
            issuer = environments[JwtConfig.ENVIRONMENT_JWT_ISSUER],
            tokenLifeTime = environments[JwtConfig.ENVIRONMENT_JWT_TOKEN_LIFETIME].toLong(),
        )
    val authName = "auth-jwt"
    install(Authentication) {
        jwt(authName) {
            verifier(config.verifier)
            validate { credential ->
                if (credential.payload
                        .getClaim("email")
                        .asString()
                        .isNotBlank()
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    // Set up the routing for the application
    routing {
        route("/auth") {
            publicAuthRoutes(config)
        }
        authenticate(authName) {
            route("/user") {
                privateAuthRoutes(config)
            }
        }
    }
}
