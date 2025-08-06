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

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import javax.sql.DataSource

/**
 * Manages the connection to a PostgreSQL database using HikariCP for connection pooling.
 *
 * @param dotenv The [Dotenv] instance used to load local database configuration from environment variables.
 */
class PostgresDatabaseProvider(
    dotenv: Dotenv = dotenv(),
) : AbstractDatabaseProvider(dotenv) {
    override val source: DataSource =
        run {
            logger.info { "Initializing PostgreSQL database connection..." }

            val jdbcUrl = dotenv[ENVIRONMENT_DATABASE_URL]
            val username = dotenv[ENVIRONMENT_DB_USER]
            val password = dotenv[ENVIRONMENT_DB_PASSWORD]

            validateEnvironmentVariables(jdbcUrl, username, password)

            logger.debug { "Connecting to: $jdbcUrl" }
            logger.debug { "Using database user: $username" }

            val config = configureHikari(jdbcUrl, username, password)

            logger.debug {
                buildString {
                    appendLine("HikariCP Configuration:")
                    appendLine("  - JDBC URL: $jdbcUrl")
                    appendLine("  - Driver: ${config.driverClassName}")
                    appendLine("  - Max Pool Size: ${config.maximumPoolSize}")
                    appendLine("  - Auto Commit: ${config.isAutoCommit}")
                    appendLine("  - Isolation: ${config.transactionIsolation}")
                }
            }

            HikariDataSource(config)
        }

    /**
     * Configures the HikariCP connection pool.
     */
    private fun configureHikari(
        jdbcUrl: String,
        username: String,
        password: String,
    ): HikariConfig {
        val config =
            HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.driverClassName = "org.postgresql.Driver"
                this.username = username
                this.password = password
                this.maximumPoolSize = 10
                this.isAutoCommit = false
                this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                // Validate the configuration
                validate()
            }
        return config
    }
}
