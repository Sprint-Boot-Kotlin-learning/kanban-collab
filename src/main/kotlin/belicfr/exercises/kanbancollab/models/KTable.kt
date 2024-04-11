package belicfr.exercises.kanbancollab.models

import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class KTable(@Id @GeneratedValue val id: Long? = null,
             var name: String,
             val token: UUID = UUID.randomUUID(),
             @ManyToMany val members: MutableSet<KUser> = mutableSetOf(),
             @OneToMany val lists: MutableSet<KList> = mutableSetOf()) {

    fun getSomeMembers(count: Int): List<KUser>
        = this.members.take(count)
}