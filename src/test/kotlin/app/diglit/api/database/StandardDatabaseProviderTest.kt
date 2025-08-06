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

import java.sql.DriverManager
import java.sql.Statement
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

const val ADMIN_DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres"
const val TEST_DATABASE_USER = "diglit_test"
const val TEST_DATABASE_PASSWORD = "diglit_test"
const val TEST_DATABASE_NAME = "diglit_test"

/**
 * Provides a skeleton for unit tests of [DatabaseProvider] implementations that use a standard database setup.
 * The [provider] should be initialized in the [setup] method, and it will be connected to a test database after
 * the setup is complete. It is recommended to late initialize the [provider] to ensure that the database is ready
 * before any tests are run.
 */
abstract class StandardDatabaseProviderTest : BaseDatabaseProviderTest() {
    /**
     * The database URL for the test database.
     */
    protected abstract val dbUrl: String

    /**
     * Sets up the test environment, including creating the test database and initializing the [provider].
     */
    @BeforeTest
    abstract fun setup()

    /**
     * Cleans up the test environment, including dropping the test database.
     */
    @AfterTest
    abstract fun tearDown()

    /**
     * Executes the given SQL statements as an admin user.
     */
    protected fun executeAsAdmin(statements: (Statement) -> Unit) {
        DriverManager
            .getConnection(
                ADMIN_DATABASE_URL,
                TEST_DATABASE_USER, // or "postgres" if "admin" doesn’t yet exist
                TEST_DATABASE_PASSWORD,
            ).use { conn ->
                conn.createStatement().use(statements)
            }
    }

    @Test
    override fun `connects only once`() {
        provider.connect()
        provider.connect()
        assertTrue { provider.isConnected() }
    }

    @Test
    override fun `disconnects when connected`() {
        provider.connect()
        provider.disconnect()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `does nothing when already disconnected`() {
        provider.disconnect()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `closes when connected`() {
        provider.connect()
        provider.close()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `does nothing when already closed`() {
        provider.close()
        assertFalse { provider.isConnected() }
    }
}
