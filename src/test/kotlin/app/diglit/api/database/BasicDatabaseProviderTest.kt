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

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Provides a base class for unit tests of [DatabaseProvider] implementations that use a basic JDBC database setup.
 *
 * Initializes a temporary test database and loads environment variables from a `.env` file in the test resources'
 * directory. The database is deleted after each test.
 *
 * @param url The JDBC-style connection URL.
 * @param fileName The name of the `.env` file to load.
 */
abstract class BasicDatabaseProviderTest(
    val url: String,
    val fileName: String,
) : BaseDatabaseProviderTest() {
    /**
     * The test database manager.
     */
    private val dbms: DBMS = DBMS(url)

    /**
     * The database provider under test.
     */
    override lateinit var provider: DatabaseProvider

    /**
     * Creates the test database and initializes the [provider] before each test.
     */
    @BeforeTest
    fun setup() {
        // Force clean state if some error prevented the database from being deleted
        dbms.deleteDatabase()
        dbms.createDatabase()
        val env =
            dotenv {
                ignoreIfMalformed = true
                ignoreIfMissing = true
                directory = directoryEnv
                filename = fileName
            }
        provider = createProvider(env)
    }

    /**
     * Deletes the test database after each test run.
     */
    @AfterTest
    fun tearDown() {
        dbms.deleteDatabase()
    }

    /**
     * Creates the [DatabaseProvider] with the [environments].
     *
     * @return the database provider instance to test.
     */
    protected abstract fun createProvider(environments: Dotenv): DatabaseProvider

    @Test
    override fun `TC08 - connect when disconnected establishes connection`() {
        provider.connect()
        provider.connect()
        assertTrue { provider.isConnected() }
    }

    @Test
    override fun `TC09 - connect when already connected does nothing`() {
        provider.connect()
        provider.connect()
        assertTrue { provider.isConnected() }
    }

    @Test
    override fun `TC10 - disconnect when connected closes connection`() {
        provider.connect()
        provider.disconnect()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `TC11 - disconnect when already disconnected does nothing`() {
        provider.disconnect()
        provider.disconnect()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `TC12 - close when connected closes connection`() {
        provider.connect()
        provider.close()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `TC13 - close when already disconnected does nothing`() {
        provider.disconnect()
        provider.close()
        assertFalse { provider.isConnected() }
    }

    @Test
    override fun `TC14 - isConnected is true after connect`() {
        provider.connect()
        assertTrue { provider.isConnected() }
    }

    @Test
    override fun `TC15 - isConnected is false after disconnect`() {
        provider.connect()
        provider.disconnect()
        assertFalse { provider.isConnected() }
    }

    companion object {
        /**
         * The path to the test environment directory.
         */
        private val directoryEnv =
            "src/test/resources/${BasicDatabaseProviderTest::class.java.`package`.name.replace('.', '/')}"
    }
}
