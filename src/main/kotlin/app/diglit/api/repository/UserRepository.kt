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
import org.jetbrains.exposed.sql.updateReturning
import kotlin.time.ExperimentalTime

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
                    .selectAll()
                    .where { Users.email eq email }
                    .singleOrNull()
                    ?.toUser()
                    ?.also {
                        logger.info("User found for email: $email")
                    }

            if (user == null) {
                logger.warn { "User not found for email: $email" }
                throw UserNotFoundException()
            }

            user
        }

    /**
     * Retiurns `true` if a [User] with the given [email] exists in the database.
     */
    fun exists(email: String): Boolean =
        transaction {
            logger.info { "Checking if user exists for email: $email" }
            !Users.selectAll().where { Users.email eq email }.empty().also {
                logger.info { "User existence check for email '$email': $it" }
            }
        }

    /**
     * Creates a new [User] with the provided details ([email], [firstName], [lastName], hashed [password]) in
     * the database.
     *
     * @return The newly created [User] object.
     * @throws UserAlreadyRegisteredException if the user with the given email already exists.
     */
    @OptIn(ExperimentalTime::class)
    fun create(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
    ): User =
        transaction {
            logger.info { "Creating user with email: $email" }

            if (exists(email)) {
                logger.warn { "Creation failed: User already exists for email '$email'" }
                throw UserAlreadyRegisteredException()
            }
            Users
                .insert {
                    it[Users.email] = email
                    it[Users.firstName] = firstName
                    it[Users.lastName] = lastName
                    it[Users.hashedPassword] = hasher.hash(password)
                }.resultedValues
                ?.first()
                ?.toUser()
                ?.also {
                    logger.info { "User created successfully with email: $email" }
                }!!
        }

    /**
     * Authenticates a user by verifying their email and password against the database.
     *
     * @throws InvalidCredentialsException if the provided email or password is incorrect.
     */
    fun authenticate(
        email: String,
        password: String,
    ): User =
        transaction {
            logger.info { "Attempting authentication for email: $email" }

            val user =
                runCatching { findByEmail(email) }
                    .getOrElse {
                        logger.warn { "Authentication failed: User not found for email '$email'" }
                        throw InvalidCredentialsException()
                    }

            if (!hasher.verify(password, user.hashedPassword)) {
                logger.warn { "Authentication failed: Incorrect password for email '$email'" }
                throw InvalidCredentialsException()
            }

            logger.info { "User authenticated successfully for email: $email" }
            user
        }

    /**
     * Deletes a [User] by their [email] address from the database.
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
     * Retrieves a [User] by [email] and verifies that the given [password] matches the stored hash.
     *
     * @return The authenticated [User] if verification succeeds.
     *
     * @throws InvalidCredentialsException If the password is incorrect.
     * @throws UserNotFoundException If no user with the given [email] exists.
     */
    private fun verifyPassword(
        email: String,
        password: String,
    ): User {
        val user = findByEmail(email)

        if (!hasher.verify(password, user.hashedPassword)) {
            logger.warn { "Password verification failed for email '$email'" }
            throw InvalidCredentialsException()
        }

        return user
    }

    /**
     * Changes the password for a [User] and verifies the old password before updating it in the database.
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

            verifyPassword(email, oldPassword)
            Users
                .updateReturning(
                    returning = Users.columns,
                    where = { Users.email eq email },
                ) {
                    it[Users.hashedPassword] = hasher.hash(newPassword)
                }.single()
                .toUser()
                .also {
                    logger.info { "Password updated for user with email: $email" }
                }
        }

    /**
     * Changes the user's [oldEmail] to the [newEmail] after verifying their [password].
     *
     * @return the updated [User] object with the new email.
     * @throws InvalidCredentialsException If the password is incorrect.
     * @throws UserAlreadyRegisteredException If the new email is already taken.
     * @throws UserNotFoundException If the user with [oldEmail] does not exist.
     */
    fun changeEmail(
        oldEmail: String,
        newEmail: String,
        password: String,
    ): User =
        transaction {
            logger.info { "Attempting email change from $oldEmail to $newEmail" }

            verifyPassword(oldEmail, password)

            if (exists(newEmail)) {
                logger.warn { "Email change failed: new email $newEmail is already in use" }
                throw UserAlreadyRegisteredException()
            }

            Users
                .updateReturning(
                    returning = Users.columns,
                    where = { Users.email eq oldEmail },
                ) {
                    it[email] = newEmail
                }.single()
                .toUser()
                .also {
                    logger.info { "Email successfully changed to $newEmail for user $oldEmail" }
                }
        }
}
