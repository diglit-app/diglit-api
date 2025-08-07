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

/**
 * Provides utility methods to programmatically manage PostgreSQL test databases.
 *
 * This class is used primarily for integration testing, allowing the creation and deletion
 * of databases before and after test execution.
 *
 * It connects to the PostgreSQL administrative database using JDBC and executes SQL statements.
 *
 * @property user The database username used to perform administrative operations.
 * @property password The corresponding password for the [user].
 */
class DBMS(
    val user: String = TEST_USER,
    val password: String = TEST_PASSWORD,
) {
    /**
     * Executes a block of SQL statements using a connection to the admin database.
     *
     * @param statements The block of statements to execute using the opened [Statement].
     */
    private fun execute(statements: (Statement) -> Unit) =
        DriverManager.getConnection(URL_ADMIN, user, password).use { conn ->
            conn.createStatement().use(statements)
        }

    /**
     * Creates a new test database with the [name] and [owner].
     */
    fun createDatabase(
        name: String = TEST_NAME,
        owner: String = TEST_USER,
    ) = execute {
        it.executeUpdate("CREATE DATABASE $name OWNER $owner")
    }

    /**
     * Drops the test database with the [name], if it exists.
     */
    fun deleteDatabase(name: String = TEST_NAME) =
        execute {
            it.executeUpdate("DROP DATABASE IF EXISTS $name")
        }

    companion object {
        /**
         * TheJDBC URL for connecting to the PostgreSQL admin database.
         */
        const val URL_ADMIN = "jdbc:postgresql://localhost:5432/postgres"

        /**
         * The test database username.
         */
        const val TEST_USER = "diglit_test"

        /**
         * The test database password.
         */
        const val TEST_PASSWORD = "diglit_test"

        /**
         * The name for the test database.
         */
        const val TEST_NAME = "diglit_test"
    }
}
