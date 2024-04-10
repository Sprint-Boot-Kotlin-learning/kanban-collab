package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KCard
import org.springframework.data.repository.CrudRepository

interface CardRepository: CrudRepository<KCard, Long> {
}