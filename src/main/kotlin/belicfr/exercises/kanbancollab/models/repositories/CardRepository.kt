package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KCard
import belicfr.exercises.kanbancollab.models.KList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface CardRepository: JpaRepository<KCard, Long> {

    fun findAllByListOrderByIdDesc(list: KList): List<KCard>

}