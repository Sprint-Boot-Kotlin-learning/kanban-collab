package belicfr.exercises.kanbancollab.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity
class KCard(@Id @GeneratedValue val id: Long? = null,
            var title: String,
            var description: String,
            var endDate: LocalDate = LocalDate.now(),
            @OneToMany val contributors: MutableSet<KUser> = mutableSetOf(),
            @ManyToOne val list: KList) {


}