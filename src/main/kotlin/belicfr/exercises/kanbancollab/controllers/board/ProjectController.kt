package belicfr.exercises.kanbancollab.controllers.board

import belicfr.exercises.kanbancollab.models.KList
import belicfr.exercises.kanbancollab.models.KTable
import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.ListRepository
import belicfr.exercises.kanbancollab.models.repositories.TableRepository
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping("/board/project")
class ProjectController(private val tableRepository: TableRepository,
                        private val listRepository: ListRepository) {

    private lateinit var project: KTable

    @GetMapping("/{token}", "/{token}/")
    fun renderProject(@PathVariable("token") token: UUID,
                      model: Model,
                      session: HttpSession): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        var nextPosition: Int

        if (this.isProjectHavingLists()) {
            val lastPositionObject: KList
                = listRepository.findFirstByTableOrderByIdDesc(project) as KList

            nextPosition = lastPositionObject.position + 1
        } else {
            nextPosition = 1
        }

        model["user"] = user
        model["project"] = project
        model["orderedLists"] = listRepository.findAllByOrderByPosition()
        model["nextPosition"] = nextPosition

        return "Project"
    }

    private fun isProjectExisting(token: UUID): Boolean {
        val projectCountWithGivenToken: Int
            = tableRepository.countAllByToken(token)

        return projectCountWithGivenToken > 0
    }

    private fun isProjectHavingLists(): Boolean
        = listRepository.countAllByTable(project) > 0

}