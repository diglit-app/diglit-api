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

import io.ktor.utils.io.core.Closeable
import mu.KLogger
import mu.KotlinLogging

/**
 * Logs operations related to database connections.
 */
val logger: KLogger = KotlinLogging.logger {}

/**
 * Environment variable for the database JDBC URL.
 * Used to retrieve the database connection URL from the environment.
 */
const val ENVIRONMENT_DATABASE_URL = "JDBC_DATABASE_URL"

/**
 * Environment variable for the database user.
 * Used to retrieve the username required to connect to the database.
 */
const val ENVIRONMENT_DB_USER = "DB_USER"

/**
 * Environment variable for the database password.
 * Used to retrieve the password required to connect to the database.
 */
const val ENVIRONMENT_DB_PASSWORD = "DB_PASSWORD"

/**
 * Manages the connection to a relational database.
 *
 * The following environment variables must be set:
 * - `JDBC_DATABASE_URL` - The JDBC URL of the database.
 * - `DB_USER` - The username for accessing the database.
 * - `DB_PASSWORD` - The password for accessing the database.
 */
interface DatabaseProvider : Closeable {
    /**
     * Validates environment variables required for the database connection.
     *
     * @param jdbcUrl The JDBC URL of the database.
     * @param username The username for accessing the database.
     * @param password The password for accessing the database.
     */
    fun validateEnvironmentVariables(
        jdbcUrl: String?,
        username: String?,
        password: String?,
    ) {
        val names =
            listOf(
                ENVIRONMENT_DATABASE_URL,
                ENVIRONMENT_DB_USER,
                ENVIRONMENT_DB_PASSWORD,
            )
        val values = listOf(jdbcUrl, username, password)
        names.zip(values).forEach { (name, value) ->
            if (value.isNullOrBlank()) {
                logger.error { "Environment variable $name is not set or empty." }
                throw IllegalStateException("$name must be provided")
            }
        }
    }

    /**
     * Returns `true` if the database connection has been established.
     */
    fun isConnected(): Boolean

    /**
     * Establishes a connection to the database.
     *
     * Should be called once during application startup.
     * Additional calls will have no effect.
     */
    fun connect()

    /**
     * Closes the database connection if it was established.
     *
     * Should be called once during application shutdown to release resources.
     * Additional calls will have no effect.
     */
    fun disconnect()

    override fun close()
}
