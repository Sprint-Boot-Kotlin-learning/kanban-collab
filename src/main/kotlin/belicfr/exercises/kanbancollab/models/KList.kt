package belicfr.exercises.kanbancollab.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
class KList(@Id @GeneratedValue val id: Long?,
            var name: String,
            var position: Int,
            @ManyToOne val table: KTable,
            @OneToMany val cards: Set<KCard>) {


}