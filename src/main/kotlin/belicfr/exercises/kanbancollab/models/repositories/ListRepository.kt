package belicfr.exercises.kanbancollab.models.repositories

import belicfr.exercises.kanbancollab.models.KList
import belicfr.exercises.kanbancollab.models.KTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface ListRepository: JpaRepository<KList, Long> {

    fun findAllByTableOrderByPosition(table: KTable): List<KList>

    fun findFirstByTableOrderByPositionDesc(table: KTable): KList?

    fun countAllByTable(table: KTable): Int

    fun findAllByPositionIsGreaterThanEqualAndIdIsNot(position: Int, id: Long): List<KList>

    fun findKListByPosition(position: Int): KList?

    fun countAllByName(name: String): Int

}