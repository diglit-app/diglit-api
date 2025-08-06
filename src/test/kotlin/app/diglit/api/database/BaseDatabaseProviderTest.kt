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

import org.junit.jupiter.api.Test

/**
 * Defines the common unit tests for [DatabaseProvider].
 */
abstract class BaseDatabaseProviderTest {
    /**
     * The database provider under test.
     */
    protected abstract var provider: DatabaseProvider

    /**
     * Verifies that calling [DatabaseProvider.connect] multiple times establishes the connection only once.
     */
    @Test
    abstract fun `connects only once`()

    /**
     * Verifies that [DatabaseProvider.disconnect] properly terminates an active connection.
     */
    @Test
    abstract fun `disconnects when connected`()

    /**
     * Verifies that calling [DatabaseProvider.disconnect] when already closed is a safe no-op.
     */
    @Test
    abstract fun `does nothing when already disconnected`()

    /**
     * Verifies that [DatabaseProvider.close] properly terminates an active connection.
     */
    @Test
    abstract fun `closes when connected`()

    /**
     * Verifies that calling [DatabaseProvider.close] when already closed is a safe no-op.
     */
    @Test
    abstract fun `does nothing when already closed`()
}
