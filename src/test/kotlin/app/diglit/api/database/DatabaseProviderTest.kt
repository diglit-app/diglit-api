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
package app.diglit.api.database

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.spy
import kotlin.test.Test

/**
 * Defines unit tests for [DatabaseProvider].
 *
 * These tests apply the Classification Tree Method (CTM) using Equivalence Class Partitioning (ECP).
 *
 * The classification tree below models the valid and invalid input spaces, and each branch is covered by a
 * corresponding test case.
 * ```
 * validateEnvironmentVariables()
 * ├── jdbcUrl
 * │   ├── Non-empty string (+)      → TC01
 * │   ├── Null (-)                  → TC02
 * │   └── Blank string (-)          → TC03
 * ├── username
 * │   ├── Non-empty string (+)      → TC01
 * │   ├── Null (-)                  → TC04
 * │   └── Blank string (-)          → TC05
 * └── password
 *     ├── Non-empty string (+)      → TC01
 *     ├── Null (-)                  → TC06
 *     └── Blank string (-)          → TC07
 *
 * ```
 *
 * @constructor Creates the test suite using a mock [DatabaseProvider].
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
open class DatabaseProviderTest {
    /**
     * The [DatabaseProvider] instance under test.
     * This is a spy to test the behavior of the implemented methods without needing a real database connection.
     */
    private val provider: DatabaseProvider = spy()

    /**
     * TC01 – All environment variables are valid.
     */
    @Test
    fun `TC01 - all values valid`() {
        assertDoesNotThrow {
            provider.validateEnvironmentVariables(
                jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                username = "user",
                password = "pass",
            )
        }
    }

    /**
     * TC02 – JDBC URL is null.
     */
    @Test
    fun `TC02 - jdbcUrl is null`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(
                jdbcUrl = null,
                username = "user",
                password = "pass",
            )
        }
    }

    /**
     * TC03 – JDBC URL is blank.
     */
    @Test
    fun `TC03 - jdbcUrl is blank`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(
                jdbcUrl = "   ",
                username = "user",
                password = "pass",
            )
        }
    }

    /**
     * TC04 – Username is null.
     */
    @Test
    fun `TC04 - username is null`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(
                jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                username = null,
                password = "pass",
            )
        }
    }

    /**
     * TC05 – Username is blank.
     */
    @Test
    fun `TC05 - username is blank`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(
                jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                username = "   ",
                password = "pass",
            )
        }
    }

    /**
     * TC06 – Password is null.
     */
    @Test
    fun `TC06 - password is null`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(
                jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                username = "user",
                password = null,
            )
        }
    }

    /**
     * TC07 – Password is blank.
     */
    @Test
    fun `TC07 - password is blank`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(
                jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                username = "user",
                password = "",
            )
        }
    }
}
