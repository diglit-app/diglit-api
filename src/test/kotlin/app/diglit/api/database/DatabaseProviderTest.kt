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

import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import kotlin.test.Test

private const val VALID_URL = "jdbc:postgresql://localhost:5432/test"
private const val VALID_USER = "user"
private const val VALID_PASS = "password"

/**
 * Defines the unit tests for [DatabaseProvider].
 */
open class DatabaseProviderTest {
    protected open val provider: DatabaseProvider = Mockito.spy()

    @Test
    fun `throws if jdbcUrl is null`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(null, VALID_USER, VALID_PASS)
        }
    }

    @Test
    fun `throws if username is blank`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(VALID_URL, " ", VALID_PASS)
        }
    }

    @Test
    fun `throws if password is null`() {
        assertThrows<IllegalStateException> {
            provider.validateEnvironmentVariables(VALID_URL, VALID_USER, null)
        }
    }

    @Test
    fun `does not throw if all values are valid`() {
        provider.validateEnvironmentVariables(VALID_URL, VALID_USER, VALID_PASS)
    }
}
