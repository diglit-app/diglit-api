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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Defines the unit tests for [AbstractDatabaseProvider].
 */
class AbstractDatabaseProviderTest : BaseDatabaseProviderTest() {
    /**
     * The mocked [DataSource] used for testing.
     */
    private val source: DataSource = mock(DataSource::class.java)

    /**
     * The mocked [Connection] used for testing.
     */
    private val connection: Connection = mock(Connection::class.java)

    override var provider: DatabaseProvider =
        object : AbstractDatabaseProvider() {
            override val source: DataSource = this@AbstractDatabaseProviderTest.source

            public override fun validateEnvironmentVariables(
                jdbcUrl: String?,
                username: String?,
                password: String?,
            ) {
                super.validateEnvironmentVariables(jdbcUrl, username, password)
            }
        }

    @BeforeEach
    fun setup() {
        `when`(source.connection).thenReturn(connection)
    }

    @Test
    override fun `connects only once`() {
        provider.connect()
        assertTrue(provider.isConnected())

        provider.connect() // should be no-op
        verify(source, never()).connection
    }

    @Test
    override fun `disconnects when connected`() {
        provider.connect()
        provider.disconnect()
        assertFalse(provider.isConnected())
        verify(connection).close()
    }

    @Test
    override fun `does nothing when already disconnected`() {
        provider.disconnect()
        assertFalse(provider.isConnected())
        verify(connection, never()).close()
    }

    @Test
    override fun `closes when connected`() {
        provider.connect()
        provider.close()
        assertFalse(provider.isConnected())
    }

    @Test
    override fun `does nothing when already closed`() {
        provider.close()
        assertFalse(provider.isConnected())
        verify(connection, never()).close()
    }
}
