package belicfr.exercises.kanbancollab.models

import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.mindrot.jbcrypt.BCrypt

@Entity
class KUser(@Id @GeneratedValue val id: Long?,
            val name: Username,
            val email: EmailAddress,
            val password: Password
) {

    @Embeddable
    data class Username(val username: String) {
        companion object {
            private const val USERNAME_MIN_LENGTH = 3
            private const val USERNAME_MAX_LENGTH = 15

            fun isValidUsername(username: String): Boolean {
                // TODO: verify if username already exists!!
                return username.length in USERNAME_MIN_LENGTH..USERNAME_MAX_LENGTH
                // [..] operator does not exclude MIN and MAX values
                //     MIN..MAX
                // to exclude MIN and MAX, use following pattern:
                //     (MIN + 1) until MAX
            }
        }
    }

    @Embeddable
    data class EmailAddress(val address: String) {
        companion object {
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