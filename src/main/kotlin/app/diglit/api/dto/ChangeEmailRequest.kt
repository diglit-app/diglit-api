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

import kotlinx.serialization.Serializable

/**
 * Request payload for changing a user's email.
 *
 * @property newEmail The new email address to be set for the user.
 * @property password The current password of the user, used for authentication before changing the email.
 */
@Serializable
data class ChangeEmailRequest(
    val newEmail: String,
    val password: String,
)
