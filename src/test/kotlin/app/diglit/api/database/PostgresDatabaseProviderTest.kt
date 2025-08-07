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
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder

/**
 * A unit test class for [PostgresDatabaseProvider].
 *
 * Sets up a PostgreSQL test database using `postgres.env` and verifies that the provider behaves as expected.
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
class PostgresDatabaseProviderTest : BasicDatabaseProviderTest(DBMS.TEST_NAME, "postgres.env") {
    override fun createProvider(environments: Dotenv): DatabaseProvider = PostgresDatabaseProvider(environments)
}
