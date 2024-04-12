package belicfr.exercises.kanbancollab.models

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

        const val ACCOUNT_DOES_NOT_EXIST_ERROR: String
            = "The account does not longer exist."
    }

    @Embeddable
    data class Username(val username: String) {
        companion object {
            const val USERNAME_MIN_LENGTH: Int = 3
            const val USERNAME_MAX_LENGTH: Int = 15

            const val USERNAME_LENGTH_ERROR: String
                = "The username must contains between $USERNAME_MIN_LENGTH " +
                  "and $USERNAME_MAX_LENGTH."

            fun isValidUsername(username: String): Boolean {
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

        override fun toString(): String
            = this.username
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

        override fun toString(): String
            = this.address
    }

    @Embeddable
    data class Password(private val givenPassword: String) {
        companion object {
            private const val PASSWORD_MIN_LENGTH: Int = 8
            // const = PRIMITIVE ON COMPILING
            // CANNOT BE DEFINED WITH CALCULATED VALUES OR DYNAMICALLY
            // Only on companion objects!!

            const val INVALID_PASSWORD_LENGTH_ERROR: String
                = "Your password must contains at least " +
                  "$PASSWORD_MIN_LENGTH characters."

            fun isValidPassword(password: String)
                = password.length >= PASSWORD_MIN_LENGTH
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

        fun verify(password: String): Boolean
            = BCrypt.checkpw(password, this.password)

        override fun toString(): String
            = this.password
    }

    override fun equals(other: Any?): Boolean
        = this === other
          || this.javaClass === other?.javaClass
          && (other as KUser).id == this.id
}