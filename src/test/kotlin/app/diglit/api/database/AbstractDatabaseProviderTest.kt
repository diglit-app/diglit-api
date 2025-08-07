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

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for [DatabaseProvider] connection lifecycle methods.
 */
@TestMethodOrder(MethodOrderer.MethodName::class)
class AbstractDatabaseProviderTest : BaseDatabaseProviderTest() {
    /**
     * The data source used to establish the database connection.
     * This is a mock to simulate the database connection behavior without needing a real database.
     */
    private lateinit var source: DataSource

    /**
     * The connection to the database.
     * This is a mock to simulate the database connection behavior without needing a real database.
     */
    private lateinit var connection: Connection

    /**
     * The [AbstractDatabaseProvider] instance under test.
     */
    protected override lateinit var provider: FakeAbstractDatabaseProvider

    /**
     * Sets up the database provider and mocks before each test.
     */
    @BeforeEach
    fun setup() {
        source = mock()
        connection = mock()
        whenever(source.connection).thenReturn(connection)

        provider = spy(FakeAbstractDatabaseProvider(source))
    }

    @Test
    override fun `TC08 - connect when disconnected establishes connection`() {
        provider.disconnect()
        provider.connect()
        assertTrue(provider.isConnected())
    }

    @Test
    override fun `TC09 - connect when already connected does nothing`() {
        provider.connect()
        reset(provider)
        provider.connect()

        assertTrue(provider.isConnected())

        verify(provider, never()).source
    }

    @Test
    override fun `TC10 - disconnect when connected closes connection`() {
        provider.connect()
        provider.disconnect()
        assertFalse(provider.isConnected())
        verify(provider).close()
    }

    @Test
    override fun `TC11 - disconnect when already disconnected does nothing`() {
        provider.disconnect()
        reset(source)
        provider.disconnect()

        assertFalse(provider.isConnected())

        verify(provider, never()).source
    }

    override fun `TC12 - close when connected closes connection`() {
        provider.connect()
        provider.close()
        assertFalse(provider.isConnected())
        verify(connection).close()
    }

    override fun `TC13 - close when already disconnected does nothing`() {
        provider.disconnect()
        reset(source)
        provider.close()

        assertFalse(provider.isConnected())

        verify(provider, never()).source
    }

    /** TC12 – isConnected() returns true after connect(). */
    @Test
    override fun `TC14 - isConnected is true after connect`() {
        provider.connect()
        assertTrue(provider.isConnected())
    }

    @Test
    override fun `TC15 - isConnected is false after disconnect`() {
        provider.connect()
        provider.disconnect()
        assertFalse(provider.isConnected())
    }
}

/**
 * A fake implementation of [AbstractDatabaseProvider] for testing purposes by exposing necessary methods and
 * properties.
 *
 * @property source The data source used to establish the database connection.
 */
class FakeAbstractDatabaseProvider(
    public override val source: DataSource,
) : AbstractDatabaseProvider(mock()) {
    override fun close() {
        connected = false
    }
}
