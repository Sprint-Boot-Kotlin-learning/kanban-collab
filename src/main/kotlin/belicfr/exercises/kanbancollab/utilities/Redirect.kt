package belicfr.exercises.kanbancollab.utilities

import org.springframework.web.servlet.view.RedirectView

object Redirect {
    fun to(route: String): RedirectView
        = RedirectView(route)
}