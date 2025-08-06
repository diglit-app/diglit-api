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
package app.diglit.api.database.schema

import app.diglit.api.repository.User
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * A schema object representing the `users` table in the database.
 *
 * @see User
 */
object Users : Table() {
    /**
     * The unique identifier for the user.
     */
    val id = uuid("id").autoGenerate()

    /**
     * The first name of the user.
     */
    val firstName = varchar("first_name", 64)

    /**
     * The last name of the user.
     */
    val lastName = varchar("last_name", 64)

    /**
     * The email address of the user.
     */
    val email = varchar("email", 128).uniqueIndex()

    /**
     * The hashed password of the user.
     */
    val hashedPassword = varchar("hashed_password", 255)

    /**
     * The date and time when the user was created.
     */
    @OptIn(ExperimentalTime::class)
    val createdAt =
        datetime("created_at").clientDefault {
            Clock.System.now().toLocalDateTime(TIME_ZONE)
        }

    /**
     * The date and time when the user was last updated.
     */
    @OptIn(ExperimentalTime::class)
    val updatedAt =
        datetime("updated_at").clientDefault {
            Clock.System.now().toLocalDateTime(TIME_ZONE)
        }

    override val primaryKey = PrimaryKey(id)

    /**
     * The time zone used for date and time fields in this table.
     */
    val TIME_ZONE = TimeZone.UTC
}
