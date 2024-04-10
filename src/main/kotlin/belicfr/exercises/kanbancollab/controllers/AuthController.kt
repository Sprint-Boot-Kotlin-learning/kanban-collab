package belicfr.exercises.kanbancollab.controllers

import belicfr.exercises.kanbancollab.utilities.Redirect
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping("/auth")
class AuthController {

    @GetMapping("", "/")
    fun renderLogin(model: Model): String {
        return "Login"
    }

    @PostMapping("/login", "/login/")
    fun login(@RequestParam("login") login: String,
              @RequestParam("password") password: String,
              model: Model): RedirectView {

        return Redirect.to("/auth")  // TODO: change to /board route
    }

    @GetMapping("/register", "/register/")
    fun renderRegister(model: Model): String {
        return "Register"
    }

}