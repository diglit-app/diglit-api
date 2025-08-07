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
package app.diglit.api.dto

import app.diglit.api.repository.User
import kotlinx.serialization.Serializable

/**
 * Request payload for registering a new user.
 *
 * Contains the necessary information required to create a new [User] account.
 *
 * @property email The user's email address. Must be unique and valid.
 * @property firstName The user's first name.
 * @property lastName The user's last name.
 * @property password The user's plaintext password.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
)
