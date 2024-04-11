package belicfr.exercises.kanbancollab.controllers

import belicfr.exercises.kanbancollab.exceptions.NotAuthenticatedException
import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import belicfr.exercises.kanbancollab.utilities.Redirect
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView
import java.util.Optional

@Controller
class Middleware(val session: HttpSession,
                 val userRepository: UserRepository) {

    companion object {
        fun redirectIfNotLogged(session: HttpSession) {
            if (session.getAttribute("user") == null) {
                throw NotAuthenticatedException()
            }
        }
    }

    @GetMapping("", "/")
    fun rootRelativeToSessionState(): RedirectView {
        val currentSessionUserId: Long
            = (session.getAttribute("user") as? Long) ?: -1

        val currentSessionUser: Optional<KUser>
            = userRepository.findById(currentSessionUserId)

        if (!currentSessionUser.isPresent) {
            return Redirect.to("/auth")
        } else {
            return Redirect.to("/board")
        }
    }
}