package belicfr.exercises.kanbancollab.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany

@Entity
class KTable(@Id @GeneratedValue val id: Long?,
             var name: String,
             @ManyToMany val members: Set<KUser>,
             @OneToMany val lists: Set<KList>) {

    
}