package belicfr.exercises.kanbancollab.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class LoginController {

    @GetMapping("", "/")
    fun render(model: Model): String {
        // TODO: login view rendering
        return ""  // TODO STUB
    }

}