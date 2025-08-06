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

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

/**
 * Signals that a user with the specified email is already registered in the database.
 */
@Serializable
class UserAlreadyRegisteredException(
    val email: String,
) : ApiException(
        HttpStatusCode.Conflict,
        "User with email $email is already registered",
    )
