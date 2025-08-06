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
package app.diglit.api.util

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [HttpStatusCode] to convert it to and from a JSON string.
 *
 * This serializer ensures that UUIDs are encoded as ints (e.g., 404 for Not Found)
 * and correctly parsed back into [HttpStatusCode] instances during deserialization.
 */
object HttpStatusCodeSerializer : KSerializer<HttpStatusCode> {
    override val descriptor =
        PrimitiveSerialDescriptor(
            HttpStatusCode::class.qualifiedName!!,
            PrimitiveKind.INT,
        )

    override fun serialize(
        encoder: Encoder,
        value: HttpStatusCode,
    ) = encoder.encodeInt(value.value)

    override fun deserialize(decoder: Decoder): HttpStatusCode = HttpStatusCode.fromValue(decoder.decodeInt())
}
