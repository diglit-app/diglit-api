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

import app.diglit.api.util.HttpStatusCodeSerializer
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

/**
 * Signals an error that occurs during API interaction.
 *
 * @property code The HTTP status code associated with the exception
 * @property message An optional custom error message that provides additional information about the exception.
 */
@Serializable
open class ApiException(
    @Serializable(HttpStatusCodeSerializer::class)
    val code: HttpStatusCode,
    override val message: String? = null,
) : RuntimeException()
