package belicfr.exercises.kanbancollab.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class KTable(@Id @GeneratedValue val id: Long? = null,
             var name: String,
             val token: UUID = UUID.randomUUID(),
             @ManyToMany val members: MutableSet<KUser> = mutableSetOf(),
             @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
                val lists: MutableSet<KList> = mutableSetOf()) {

    companion object {
        const val NAME_MAX_LENGTH: Int = 50

        const val INVALID_NAME_LENGTH_ERROR: String
            = "The name must contain less than " +
              "$NAME_MAX_LENGTH characters."

        fun isNameValid(name: String): Boolean
            = name.length in 1..NAME_MAX_LENGTH
    }

    fun getSomeMembers(count: Int): List<KUser>
        = this.members.take(count)
}