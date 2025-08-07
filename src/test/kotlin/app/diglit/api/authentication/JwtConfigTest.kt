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

import com.auth0.jwt.exceptions.JWTVerificationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Defines unit tests for [JwtConfig].
 *
 * The classification tree below models the valid and invalid input spaces, and each branch is covered by a
 * corresponding test case.
 * ```
 * ├── JwtConfig(secret, issuer, tokenLifeTime)
 * │   ├── secret
 * │   │   ├── non-empty                     → TC01
 * │   │   └── empty                         → TC02
 * │   ├── issuer
 * │   │   ├── non-empty                     → TC01, others
 * │   │   └── empty                         → TC03
 * │   └── tokenLifeTime
 * │       ├── positive (>0)                 → TC01, TC02, TC06, TC07, TC05
 * │       ├── zero (0)                      → TC03, TC08
 * │       └── negative (<0)                 → TC04
 * ├── generateEmailToken(email)
 * │   ├── well-formed email                 → TC01
 * │   └── empty string                      → TC05
 * └── verifier.verify(token)
 *     ├── valid signature                   → TC01
 *     ├── bad signature                     → TC06
 *     ├── matching issuer                   → TC01
 *     ├── mismatched issuer                 → TC07
 *     ├── not expired                       → TC01
 *     └── expired (zero/negative TTL)       → TC03, TC04, TC08
 * ```
 */
class JwtConfigTest {
    /**
     * The default secret used for signing JWT tokens in tests.
     */
    private val defaultSecret = "mySecret"

    /**
     * The default issuer used for JWT tokens in tests.
     */
    private val defaultIssuer = "myApp"

    /**
     * TC01 valid parameters and well-formed email should verify successfully
     */
    @Test
    fun `TC01 valid parameters and well-formed email should verify successfully`() {
        val config = JwtConfig(defaultSecret, defaultIssuer)
        val email = "user@example.com"
        val token = config.generateEmailToken(email)
        val decoded = config.verifier.verify(token)

        assertEquals(email, decoded.getClaim("email").asString())
    }

    /**
     * TC02 empty secret should throw IllegalArgumentException on construction
     */
    @Test
    fun `TC02 empty secret should throw IllegalArgumentException on construction`() {
        assertThrows<IllegalArgumentException> {
            JwtConfig("", defaultIssuer, 60 * 60 * 1000L)
        }
    }

    /**
     * TC03 empty issuer and zero lifetime should cause token to be expired immediately
     */
    @Test
    fun `TC03 empty issuer and zero lifetime should cause token to be expired immediately`() {
        val config = JwtConfig(defaultSecret, "", 0L)
        val token = config.generateEmailToken("user@example.com")

        assertThrows<JWTVerificationException> {
            config.verifier.verify(token)
        }
    }

    /**
     * TC04 negative lifetime should cause token to be expired
     */
    @Test
    fun `TC04 negative lifetime should cause token to be expired`() {
        val config = JwtConfig(defaultSecret, defaultIssuer, -1000L)
        val token = config.generateEmailToken("user@example.com")

        assertThrows<JWTVerificationException> {
            config.verifier.verify(token)
        }
    }

    /**
     * TC05 empty email claim should verify successfully and claim be empty
     */
    @Test
    fun `TC05 empty email claim should verify successfully and claim be empty`() {
        val config = JwtConfig(defaultSecret, defaultIssuer)
        val token = config.generateEmailToken("")
        val decoded = config.verifier.verify(token)

        assertEquals("", decoded.getClaim("email").asString())
    }

    /**
     * TC06 bad signature should fail verification
     */
    @Test
    fun `TC06 bad signature should fail verification`() {
        val config1 = JwtConfig(defaultSecret, defaultIssuer)
        val config2 = JwtConfig("otherSecret", defaultIssuer)
        val token = config2.generateEmailToken("user@example.com")

        assertThrows<JWTVerificationException> {
            config1.verifier.verify(token)
        }
    }

    /**
     * TC07 issuer mismatch should fail verification
     */
    @Test
    fun `TC07 issuer mismatch should fail verification`() {
        val config1 = JwtConfig(defaultSecret, "issuerA")
        val config2 = JwtConfig(defaultSecret, "issuerB")
        val token = config2.generateEmailToken("user@example.com")

        assertThrows<JWTVerificationException> {
            config1.verifier.verify(token)
        }
    }

    /**
     * TC08 default TTL but expired at boundary (treated as expired)
     */
    @Test
    fun `TC08 default TTL but expired at boundary (treated as expired)`() {
        val config = JwtConfig(defaultSecret, defaultIssuer, 0L)
        val token = config.generateEmailToken("boundary@example.com")

        assertThrows<JWTVerificationException> {
            config.verifier.verify(token)
        }
    }
}
