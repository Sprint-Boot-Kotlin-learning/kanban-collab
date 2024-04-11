package belicfr.exercises.kanbancollab.controllers.board

import belicfr.exercises.kanbancollab.controllers.Middleware
import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.TableRepository
import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/board")
class BoardController(private val session: HttpSession,
                      private val tableRepository: TableRepository) {

    @ModelAttribute
    fun setup(model: Model) {
        Middleware.redirectIfNotLogged(session)
    }

    @GetMapping("", "/")
    fun render(model: Model): String {
        val user: KUser = session.getAttribute("user") as KUser

        model["user"] = user
        model["projects"] = tableRepository.findAllByMembersContainsOrderByIdDesc(
            user)

        return "Board"
    }

}