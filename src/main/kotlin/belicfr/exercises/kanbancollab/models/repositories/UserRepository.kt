package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KUser
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<KUser, Long> {
}