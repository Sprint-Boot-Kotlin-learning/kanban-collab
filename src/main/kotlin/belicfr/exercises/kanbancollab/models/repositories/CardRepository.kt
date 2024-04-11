package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KCard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface CardRepository: JpaRepository<KCard, Long> {
}