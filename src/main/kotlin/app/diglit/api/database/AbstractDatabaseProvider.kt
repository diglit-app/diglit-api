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
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

/**
 * Provides a skeletal implementation of the [DatabaseProvider] interface to minimize the effort required
 * for implementing database connection management by defining common logic for connecting and disconnecting
 * from a relational database using a [DataSource].
 *
 * Subclasses are expected to provide the specific [DataSource] that will be used to manage database connections.
 * This class helps implementers focus on the specific database configuration rather than the connection logic.
 *
 * @property dotenv The [Dotenv] instance used to load local database configuration from environment variables.
 * configuration.
 */
abstract class AbstractDatabaseProvider(
    protected val dotenv: Dotenv = dotenv(),
) : DatabaseProvider {
    /**
     * The data source used to establish the database connection.
     */
    protected abstract val source: DataSource

    /**
     * Whether the database connection has been established.
     */
    private var connected: Boolean = false

    /**
     * Returns true if the database has been connected.
     * Note: this reflects internal state, not the actual pool/connection health.
     */
    override fun isConnected(): Boolean = connected

    override fun connect() {
        if (connected) {
            logger.info { "Database is already connected. Skipping connection attempt." }
            return
        }

        try {
            logger.info { "Establishing connection to database..." }
            Database.connect(source)
            connected = true
            logger.info { "Database connection established successfully." }
        } catch (e: Exception) {
            logger.error(e) { "Database connection failed!" }
            throw e
        }
    }

    override fun disconnect() {
        if (!connected) {
            logger.info { "Database is not connected. Skipping disconnection attempt." }
            return
        }

        logger.info { "Closing database connection..." }

        try {
            source.connection.close()
            connected = false
            logger.info { "Database connection closed." }
        } catch (e: Exception) {
            logger.error(e) { "Failed to close database connection." }
            throw e
        }
    }
}
