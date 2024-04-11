package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KUser
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<KUser, Long> {

    fun countAllByLogin(login: KUser.Username): Int

    fun findKUserByLogin(login: KUser.Username): KUser?

}