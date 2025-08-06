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

import io.github.cdimascio.dotenv.dotenv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

const val TEST_DATABASE_URL = "jdbc:postgresql://localhost:5432/diglit_test"

/**
 * Defines the unit tests for [PostgresDatabaseProvider].
 *
 * Please ensure that a PostgreSQL role with the name [TEST_DATABASE_USER] exists and has the password
 * [TEST_DATABASE_PASSWORD].
 *
 * Use the following command to create the role if it does not exist:
 * ```
 * psql -U postgres -c "CREATE ROLE username WITH LOGIN PASSWORD 'password' CREATEDB;"
 * ```
 * where `username` is the value of [TEST_DATABASE_USER] and `'password'` is the value of [TEST_DATABASE_PASSWORD].
 */
class PostgresDatabaseProviderTest : StandardDatabaseProviderTest() {
    override lateinit var provider: DatabaseProvider

    override val dbUrl: String = TEST_DATABASE_URL

    @BeforeTest
    override fun setup() {
        executeAsAdmin {
            it.executeUpdate("DROP DATABASE IF EXISTS $TEST_DATABASE_NAME")
            it.executeUpdate("CREATE DATABASE $TEST_DATABASE_NAME OWNER $TEST_DATABASE_USER")
        }
        val dotenv =
            dotenv {
                ignoreIfMalformed = true
                ignoreIfMissing = true
                directory =
                    "src/test/resources/${PostgresDatabaseProviderTest::class.java.`package`.name.replace('.', '/')}"
                filename = "postgres_test.env"
            }

        provider = PostgresDatabaseProvider(dotenv)
    }

    @AfterTest
    override fun tearDown() {
        executeAsAdmin {
            it.executeUpdate("DROP DATABASE IF EXISTS $TEST_DATABASE_NAME")
        }
    }
}
