package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KList
import belicfr.exercises.kanbancollab.models.KTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface ListRepository: JpaRepository<KList, Long> {

    fun findAllByOrderByPosition(): List<KList>

    fun findFirstByTableOrderByIdDesc(table: KTable): KList?

    fun countAllByTable(table: KTable): Int

}