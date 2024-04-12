package belicfr.exercises.kanbancollab.controllers.board

import belicfr.exercises.kanbancollab.controllers.Middleware
import belicfr.exercises.kanbancollab.models.KList
import belicfr.exercises.kanbancollab.models.KTable
import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.ListRepository
import belicfr.exercises.kanbancollab.models.repositories.TableRepository
import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import belicfr.exercises.kanbancollab.utilities.Redirect
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView
import java.util.Optional
import java.util.UUID

@Controller
@RequestMapping("/board/project/{token}/{listId}")
class ListController(private val userRepository: UserRepository,
                     private val listRepository: ListRepository,
                     private val tableRepository: TableRepository,
                     private val session: HttpSession) {

    private lateinit var project: KTable

    @ModelAttribute
    fun setup(model: Model) {
        Middleware.redirectIfNotLogged(session)
    }

    @GetMapping("/move/left", "/move/left/")
    fun moveToLeft(@PathVariable("token") token: UUID,
                   @PathVariable("listId") listId: Long): RedirectView {

        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")
        }

        this.project = tableRepository.findKTableByToken(token) as KTable

        val listQuery: Optional<KList> = listRepository.findById(listId)
        val user: KUser = session.getAttribute("user") as KUser

        if (listQuery.isPresent
            && listQuery.get().position > 1
            && this.isUserProjectMember(user)) {

            val list: KList = listQuery.get()
            val previousListQuery: KList? = listRepository.findKListByPosition(
                list.position - 1)

            list.position--
            listRepository.save(list)

            if (previousListQuery !== null) {
                val previousList: KList = previousListQuery

                previousList.position++
                listRepository.save(previousList)
            }

            listRepository.flush()
        }

        return Redirect.to("/board/project/$token")
    }

    private fun isProjectExisting(token: UUID): Boolean {
        val projectCountWithGivenToken: Int
            = tableRepository.countAllByToken(token)

        return projectCountWithGivenToken > 0
    }

    private fun isUserProjectMember(user: KUser): Boolean
        = this.project.members.any { it == user }

}