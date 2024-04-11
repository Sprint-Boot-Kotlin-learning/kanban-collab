package belicfr.exercises.kanbancollab.controllers

import belicfr.exercises.kanbancollab.exceptions.NotAuthenticatedException
import belicfr.exercises.kanbancollab.utilities.Redirect
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.view.RedirectView

@ControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(NotAuthenticatedException::class)
    fun handleNotAuthenticated(): RedirectView {
        return Redirect.to("/auth")
    }

}