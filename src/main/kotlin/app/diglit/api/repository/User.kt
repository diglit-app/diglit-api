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
package app.diglit.api.repository

import app.diglit.api.database.schema.Users
import app.diglit.api.util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

/**
 * Represents a [User] from a database.
 *
 * @property id The unique identifier for the user
 * @property firstName The first name of the user.
 * @property lastName The last name of the user.
 * @property email The email address of the user.
 * @property hashedPassword The hashed password of the user.
 *
 * @see Users
 */
@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    @Transient
    val hashedPassword: String = "",
)

/**
 * Returns a [User] object from a [ResultRow].
 */
fun ResultRow.toUser(): User =
    User(
        id = this[Users.id],
        firstName = this[Users.firstName],
        lastName = this[Users.lastName],
        email = this[Users.email],
        hashedPassword = this[Users.hashedPassword],
    )
