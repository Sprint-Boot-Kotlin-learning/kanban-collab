package belicfr.exercises.kanbancollab.controllers.auth

import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import belicfr.exercises.kanbancollab.utilities.Redirect
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping("/auth")
class AuthController(val userRepository: UserRepository) {

    companion object {
        private const val EMPTY_REQUIRED_FIELD_ERROR: String
            = "%s input is required."

        private const val INVALID_CREDENTIALS_ERROR: String
            = "Given credentials are invalid."
    }

    @GetMapping("", "/")
    fun renderLogin(model: Model): String {
        return "Login"
    }

    @PostMapping("/login", "/login/")
    fun login(@RequestParam("login") login: String,
              @RequestParam("password") password: String,
              model: Model,
              redirectAttributes: RedirectAttributes,
              session: HttpSession): RedirectView {

        val errors: MutableList<String> = arrayListOf()

        if (login.isBlank()) {
            errors.add(String.format(EMPTY_REQUIRED_FIELD_ERROR, "Login"))
        }

        if (password.isBlank()) {
            errors.add(String.format(EMPTY_REQUIRED_FIELD_ERROR, "Password"))
        }

        if (!this.isAccountExisting(login)) {
            errors.add(INVALID_CREDENTIALS_ERROR)
        }

        val user: KUser? = userRepository.findKUserByLogin(
            KUser.Username(login))

        if (user === null) {
            errors.add(KUser.ACCOUNT_DOES_NOT_EXIST_ERROR)
        } else if (!user.password.verify(password)) {
            errors.add(INVALID_CREDENTIALS_ERROR)
        }

        if (errors.isNotEmpty()) {
            redirectAttributes.addFlashAttribute(
                "login",
                login)

            redirectAttributes.addFlashAttribute(
                "errors",
                errors)

            return Redirect.to("/auth")
        }

        session.setAttribute("user", user)

        return Redirect.to("/board")
    }

    @GetMapping("/register", "/register/")
    fun renderRegister(model: Model): String {
        return "Register"
    }

    @PostMapping("/register/create-account", "/register/create-account/")
    fun createUser(@RequestParam("firstname") firstname: String,
                   @RequestParam("lastname") lastname: String,
                   @RequestParam("login") login: String,
                   @RequestParam("email") email: String,
                   @RequestParam("password") password: String,
                   model: Model,
                   redirectAttributes: RedirectAttributes): RedirectView {

        val errors: MutableList<String> = arrayListOf()

        if (firstname.isBlank()) {
            errors.add(String.format(
                EMPTY_REQUIRED_FIELD_ERROR,
                "Firstname"))
        }

        if (lastname.isBlank()) {
            errors.add(String.format(
                EMPTY_REQUIRED_FIELD_ERROR,
                "Lastname"))
        }

        if (email.isBlank()) {
            errors.add(String.format(
                EMPTY_REQUIRED_FIELD_ERROR,
                "Email address"))
        }

        if (password.isBlank()) {
            errors.add(String.format(
                EMPTY_REQUIRED_FIELD_ERROR,
                "Password"))
        }

        if (!KUser.Username.isValidUsername(login)) {
            errors.add(KUser.Username.USERNAME_LENGTH_ERROR)
        }

        if (this.isAccountExisting(login)) {
            errors.add(KUser.LOGIN_ALREADY_USED_ERROR)
        }

        if (!KUser.EmailAddress.isValidAddress(email)) {
            errors.add(KUser.EmailAddress.INVALID_EMAIL_ADDRESS_ERROR)
        }

        if (!KUser.Password.isValidPassword(password)) {
            errors.add(KUser.Password.INVALID_PASSWORD_LENGTH_ERROR)
        }

        if (errors.isNotEmpty()) {
            redirectAttributes.addFlashAttribute(
                "firstname",
                firstname)

            redirectAttributes.addFlashAttribute(
                "lastname",
                lastname)

            redirectAttributes.addFlashAttribute(
                "login",
                login)

            redirectAttributes.addFlashAttribute(
                "email",
                email)

            redirectAttributes.addFlashAttribute(
                "password",
                password)

            redirectAttributes.addFlashAttribute(
                "errors",
                errors)

            return Redirect.to("/auth/register")
        }

        userRepository.save(KUser(
            firstname = firstname,
            lastname = lastname,
            login = KUser.Username(login),
            email = KUser.EmailAddress(email),
            password = KUser.Password(password)
        ))

        return Redirect.to("/auth")
    }

    private fun isAccountExisting(login: String): Boolean {
        if (login.length < KUser.Username.USERNAME_MIN_LENGTH) {
            return false
        }

        val userByLoginCount: Int = userRepository.countAllByLogin(KUser.Username(login))

        return userByLoginCount > 0
    }

}