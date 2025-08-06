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

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * The cost factor for the BCrypt hashing algorithm.
 * This value determines the computational cost of hashing.
 * A higher value increases security but also increases processing time.
 */
private const val COST_FACTOR = 12

/**
 * Uses the BCrypt hashing algorithm to hash and verify sensitive data.
 */
internal class BCryptHasher : Hasher {
    override fun hash(input: String): String =
        BCrypt
            .withDefaults()
            .hashToString(COST_FACTOR, input.toCharArray())

    override fun verify(
        input: String,
        hash: String,
    ): Boolean =
        BCrypt
            .verifyer()
            .verify(input.toCharArray(), hash)
            .verified
}
