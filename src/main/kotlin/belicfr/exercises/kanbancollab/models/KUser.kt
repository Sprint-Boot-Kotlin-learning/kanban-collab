package belicfr.exercises.kanbancollab.models

import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.mindrot.jbcrypt.BCrypt

@Entity
class KUser(@Id @GeneratedValue val id: Long? = null,
            val firstname: String,
            val lastname: String,
            val login: Username,
            val email: EmailAddress,
            val password: Password) {

    companion object {
        const val LOGIN_ALREADY_USED_ERROR: String
            = "This login is already used."
    }

    @Embeddable
    data class Username(val username: String) {
        companion object {
            private const val USERNAME_MIN_LENGTH: Int = 3
            private const val USERNAME_MAX_LENGTH: Int = 15

            const val USERNAME_LENGTH_ERROR: String
                = "The username must contains between $USERNAME_MIN_LENGTH " +
                  "and $USERNAME_MAX_LENGTH."

            fun isValidUsername(username: String): Boolean {
                // TODO: verify if username already exists!!
                return username.length in USERNAME_MIN_LENGTH..USERNAME_MAX_LENGTH
                // [..] operator does not exclude MIN and MAX values
                //     MIN..MAX
                // to exclude MIN and MAX, use following pattern:
                //     (MIN + 1) until MAX
            }
        }

        init {
            if (username.length !in USERNAME_MIN_LENGTH..USERNAME_MAX_LENGTH) {
                throw IllegalArgumentException(USERNAME_LENGTH_ERROR)
            }
        }
    }

    @Embeddable
    data class EmailAddress(val address: String) {
        companion object {
            const val INVALID_EMAIL_ADDRESS_ERROR
                = "The email address is invalid."

            fun isValidAddress(address: String): Boolean {
                val emailAddressRegex: Regex = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

                return address.matches(emailAddressRegex)
            }
        }

        init {
            if (!isValidAddress(this.address)) {
                throw IllegalArgumentException(
                    "Invalid email address.")
            }
        }
    }

    @Embeddable
    data class Password(private val givenPassword: String) {
        companion object {
            private const val PASSWORD_MAX_LENGTH: Int = 8
            // const = PRIMITIVE ON COMPILING
            // CANNOT BE DEFINED WITH CALCULATED VALUES OR DYNAMICALLY
            // Only on companion objects!!

            fun isValidPassword(password: String)
                = password.length >= PASSWORD_MAX_LENGTH
        }

        var password: String
            private set

        init {
            if (!isValidPassword(givenPassword)) {
                throw IllegalArgumentException(
                    "The password must contains 8 characters min.")
            }

            this.password = BCrypt.hashpw(givenPassword, BCrypt.gensalt())
        }
    }
}