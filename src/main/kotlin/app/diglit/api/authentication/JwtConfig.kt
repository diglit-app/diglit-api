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
package app.diglit.api.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Configures JWT (JSON Web Token) for authentication.
 *
 * @param secret The secret key used to sign the JWT tokens.
 * @param tokenLifeTime The duration in milliseconds for which the JWT token is valid. Default is 7 days.
 */
class JwtConfig(
    secret: String,
    val tokenLifeTime: Long = DEFAULT_TOKEN_LIFETIME,
) {
    /**
     * The algorithm used for signing and verifying tokens.
     */
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    /**
     * The verifier used to validate incoming JWT tokens.
     */
    val verifier: JWTVerifier =
        JWT
            .require(algorithm)
            .withIssuer(ISSUER)
            .build()

    /**
     * Generates a signed JWT token containing the provided [claims].  These claims will be included in the JWT token
     * payload.
     *
     * @param claims a map of claim in where the key is the claim name and the value is the claim value.
     */
    @OptIn(ExperimentalTime::class)
    private fun generateToken(claims: Map<String, String>): String {
        val builder =
            JWT
                .create()
                .withSubject("Authentication")
                .withIssuer(ISSUER)
                .withExpiresAt(Date(Clock.System.now().toEpochMilliseconds() + tokenLifeTime))

        claims.forEach { (name, value) -> builder.withClaim(name, value) }

        return builder.sign(algorithm)
    }

    /**
     * Generates a signed JWT token containing the user's [email].
     */
    fun generateEmailToken(email: String): String = generateToken(mapOf("email" to email))

    companion object {
        /**
         * The issuer of the JWT token. Used for validating the source of the token.
         */
        const val ISSUER: String = "diglit-api"

        /**
         * Default token expiration time (7 days) in milliseconds.
         */
        const val DEFAULT_TOKEN_LIFETIME: Long = 7 * 24 * 60 * 60 * 1000L

        /**
         * Name of the environment variable that stores the JWT secret key.
         */
        const val ENVIRONMENT_JWT_SECRET: String = "JWT_SECRET"

        /**
         * Name of the environment variable that stores the JWT expiration time in milliseconds.
         */
        const val ENVIRONMENT_JWT_TOKEN_LIFETIME: String = "JWT_TOKEN_LIFETIME"
    }
}
