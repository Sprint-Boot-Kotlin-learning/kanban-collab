package belicfr.exercises.kanbancollab.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class KList(@Id @GeneratedValue val id: Long? = null,
            var name: String,
            var position: Int,
            @ManyToOne val table: KTable,
            @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
                val cards: MutableSet<KCard> = mutableSetOf()) {

    companion object {
        const val NAME_MAX_LENGTH = 50

        const val INVALID_NAME_LENGTH_ERROR
            = "The name length must be between 1 and " +
              "$NAME_MAX_LENGTH characters."

        const val INVALID_POSITION_ERROR
            = "The position must be greater than 0."

        fun isNameValid(name: String): Boolean
            = name.length <= NAME_MAX_LENGTH

        fun isPositionValid(position: Int): Boolean
            = position > 0
    }
}