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
import kotlin.test.Test

/**
 * Provides a skeleton for unit tests of [DatabaseProvider] implementations that use a standard JDBC database setup.
 * The classification tree below models the valid and invalid input spaces, and each branch is covered by a
 * corresponding test case.
 * ```
 * ├── connect()
 * │   ├── When disconnected        → TC08
 * │   └── When already connected   → TC09
 * ├── disconnect()
 * │   ├── When connected           → TC10
 * │   └── When already disconnected→ TC11
 * ├── close()
 * │   ├── When connected           → TC12
 * │   └── When already disconnected→ TC13
 * └── isConnected()
 *     ├── After connect            → TC14
 *     ├── After disconnect         → TC15
 * ```
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
abstract class BaseDatabaseProviderTest : DatabaseProviderTest() {
    /**
     * The [DatabaseProvider] instance under test.
     */
    protected abstract val provider: DatabaseProvider

    /**
     * TC08 – connect() when disconnected.
     */
    @Test
    abstract fun `TC08 - connect when disconnected establishes connection`()

    /**
     * TC09 – connect() when already connected does nothing.
     */
    @Test
    abstract fun `TC09 - connect when already connected does nothing`()

    /**
     * TC10 – disconnect() when connected closes connection.
     */
    @Test
    abstract fun `TC10 - disconnect when connected closes connection`()

    /**
     * TC12 – disconnect() when already disconnected does nothing.
     */
    @Test
    abstract fun `TC11 - disconnect when already disconnected does nothing`()

    /**
     * TC10 – disconnect() when connected closes connection.
     */
    @Test
    abstract fun `TC12 - close when connected closes connection`()

    /**
     * TC12 – disconnect() when already disconnected does nothing.
     */
    @Test
    abstract fun `TC13 - close when already disconnected does nothing`()

    /** TC13 – isConnected() returns true after connect(). */
    @Test
    abstract fun `TC14 - isConnected is true after connect`()

    /**
     * TC14 – isConnected() returns false after disconnect().
     */
    @Test
    abstract fun `TC15 - isConnected is false after disconnect`()
}
