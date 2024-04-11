package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KTable
import belicfr.exercises.kanbancollab.models.KUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface TableRepository: JpaRepository<KTable, Long> {

    fun findAllByMembersContainsOrderByIdDesc(member: KUser): List<KTable>

}