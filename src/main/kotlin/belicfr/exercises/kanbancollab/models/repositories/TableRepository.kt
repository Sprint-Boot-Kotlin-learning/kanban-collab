package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KTable
import org.springframework.data.repository.CrudRepository

interface TableRepository: CrudRepository<KTable, Long> {
}