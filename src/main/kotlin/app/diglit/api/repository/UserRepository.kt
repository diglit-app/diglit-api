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
import app.diglit.api.util.Hasher
import app.diglit.api.util.HasherFactory
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Repository for managing [User] entities in the database.
 */
object UserRepository {
    /**
     * Logs [User] repository operations.
     */
    private val logger = KotlinLogging.logger {}

    /**
     * Provides security hashing functionality for sensitive data.
     */
    private val hasher: Hasher = HasherFactory.create()

    /**
     * Returns all [User]s in the database.
     */
    fun all(): List<User> =
        transaction {
            logger.info { "Fetching all users from the database" }
            Users.selectAll().map { it.toUser() }.also {
                logger.info { "Fetched ${it.size} users from the database" }
            }
        }

    /**
     * Finds a [User] by their [email] address in the database.
     *
     * @throws UserNotFoundException if no user with the given email exists.
     */
    fun findByEmail(email: String): User =
        transaction {
            logger.info { "Fetching user by email: $email" }
            val user =
                Users
                    .select(Users.email)
                    .where { Users.email eq email }
                    .singleOrNull()
                    ?.toUser()

            if (user == null) {
                logger.warn { "User not found for email: $email" }
                throw UserNotFoundException(email)
            }
            logger.info("User found for email: $email")
            user
        }

    /**
     * Creates a new [User] with the provided details ([email], [firstName], [lastName], hashed [password]).
     *
     * @return The newly created [User] object.
     * @throws UserAlreadyRegisteredException if the user with the given email already exists.
     */
    fun create(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
    ): User =
        transaction {
            logger.info { "Creating user with email: $email" }

            val hashedPassword = hasher.hash(password)
            val user =
                Users
                    .insert {
                        it[Users.email] = email
                        it[Users.firstName] = firstName
                        it[Users.lastName] = lastName
                        it[Users.hashedPassword] = hashedPassword
                    }.resultedValues
                    ?.firstOrNull()
                    ?.toUser()

            if (user == null) {
                logger.warn { "Creation failed: User already exists for email '$email'" }
                throw UserAlreadyRegisteredException(email)
            }

            logger.info { "User created successfully with email: $email" }
            user
        }

    /**
     * Deletes a [User] by their [email] address.
     *
     * @return The deleted [User] object.
     * @throws UserNotFoundException if the user with the given email does not exist.
     */
    fun deleteByEmail(email: String): User =
        transaction {
            logger.info { "Deleting user with email: $email" }
            findByEmail(email).also {
                Users.deleteWhere { Users.email eq email }
                logger.info { "Successfully deleted user: $email" }
            }
        }

    /**
     * Changes the password for a [User] and verifies the old password before updating.
     *
     * @return The updated [User] object with the new hashed password.
     * @throws UserNotFoundException if the user with the given email does not exist.
     * @throws InvalidCredentialsException if the old password does not match the stored password.
     */
    fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String,
    ): User =
        transaction {
            logger.info { "Changing password for user with email: $email" }
            val user =
                Users
                    .select(Users.hashedPassword)
                    .where { Users.email eq email }
                    .singleOrNull()
                    ?.toUser()
                    ?: run {
                        logger.warn { "Password change failed: User not found for email '$email'" }
                        throw UserNotFoundException(email)
                    }

            val storedHash = user.hashedPassword
            if (!hasher.verify(oldPassword, storedHash)) {
                logger.warn { "Password change failed: Incorrect old password for email '$email'" }
                throw InvalidCredentialsException(email)
            }

            val newHashedPassword = hasher.hash(newPassword)
            Users.update({ Users.email eq email }) {
                it[hashedPassword] = newHashedPassword
            }

            User(user, newHashedPassword).also {
                logger.info { "Password updated for user with email: $email" }
            }
        }
}
