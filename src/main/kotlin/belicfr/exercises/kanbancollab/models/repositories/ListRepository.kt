package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KList
import org.springframework.data.repository.CrudRepository

interface ListRepository: CrudRepository<KList, Long> {
}