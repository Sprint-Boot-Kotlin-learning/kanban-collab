package belicfr.exercises.kanbancollab.controllers.board

import belicfr.exercises.kanbancollab.controllers.Middleware
import belicfr.exercises.kanbancollab.controllers.auth.AuthController
import belicfr.exercises.kanbancollab.models.KCard
import belicfr.exercises.kanbancollab.models.KList
import belicfr.exercises.kanbancollab.models.KTable
import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.CardRepository
import belicfr.exercises.kanbancollab.models.repositories.ListRepository
import belicfr.exercises.kanbancollab.models.repositories.TableRepository
import belicfr.exercises.kanbancollab.utilities.Redirect
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView
import java.util.UUID

@Controller
@RequestMapping("/board/project")
class ProjectController(private val tableRepository: TableRepository,
                        private val listRepository: ListRepository,
                        private val cardRepository: CardRepository,
                        private val session: HttpSession) {

    private lateinit var project: KTable

    @ModelAttribute
    fun setup(model: Model) {
        Middleware.redirectIfNotLogged(session)
    }

    @GetMapping("/{token}", "/{token}/")
    fun renderProject(@PathVariable("token") token: UUID,
                      model: Model): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        val nextPosition: Int

        if (this.isProjectHavingLists()) {
            val lastPositionObject: KList
                = listRepository.findFirstByTableOrderByPositionDesc(project) as KList

            nextPosition = lastPositionObject.position + 1
        } else {
            nextPosition = 1
        }

        val orderedLists: List<KList> = listRepository.findAllByTableOrderByPosition(
            this.project)

        val orderedListsWithCards: Map<KList, List<KCard>> = orderedLists.associateWith { list: KList ->
            cardRepository.findAllByListOrderByIdDesc(list)
        }

        model["user"] = user
        model["project"] = project
        model["orderedLists"] = orderedListsWithCards
        model["nextPosition"] = nextPosition
        model["isMember"] = this.isUserProjectMember(user)

        return "Board/Project/Project"
    }

    @PostMapping("/lists/create")
    fun createList(@RequestParam("token") token: UUID,
                   @RequestParam("name") name: String,
                   @RequestParam("position") position: Int,
                   redirectAttributes: RedirectAttributes): RedirectView {

        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")

        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        if (!this.isUserProjectMember(user)) {
            return Redirect.to("/board/project/$token")
        }

        val errors: MutableList<String> = arrayListOf()

        if (name.isBlank()) {
            errors.add(String.format(AuthController.EMPTY_REQUIRED_FIELD_ERROR,
                                     "Name"))
        }

        if (!KList.isNameValid(name)) {
            errors.add(KList.INVALID_NAME_LENGTH_ERROR)
        }

        if (this.isListNameAlreadyTaken(name)) {
            errors.add(KList.NAME_ALREADY_TAKEN_ERROR)
        }

        if (!KList.isPositionValid(position)) {
            errors.add(KList.INVALID_POSITION_ERROR)
        }

        if (errors.isNotEmpty()) {
            redirectAttributes.addFlashAttribute("errors",
                                                 errors)
        } else {
            val list: KList = KList(
                name = name,
                position = position,
                table = project)

            listRepository.save(list)
            listRepository.flush()

            project.lists.add(list)
            tableRepository.save(project)
            tableRepository.flush()

            this.incrementNextLists(list)
        }

        return Redirect.to("/board/project/$token")
    }

    @GetMapping("/new", "/new/")
    fun renderNewProject(model: Model): String {

        val user: KUser = session.getAttribute("user") as KUser

        model["user"] = user
        model["nameMaxLength"] = KTable.NAME_MAX_LENGTH

        return "Board/Project/NewProject"
    }

    @PostMapping("/new/create", "/new/create/")
    fun createProject(@RequestParam("name") name: String,
                      redirectAttributes: RedirectAttributes): RedirectView {

        val errors: MutableList<String> = arrayListOf()

        val user: KUser = session.getAttribute("user") as KUser

        if (name.isBlank()) {
            errors.add(String.format(AuthController.EMPTY_REQUIRED_FIELD_ERROR,
                                     "Name"))
        }

        if (!KTable.isNameValid(name)) {
            errors.add(KTable.INVALID_NAME_LENGTH_ERROR)
        }

        if (errors.isNotEmpty()) {
            redirectAttributes.addFlashAttribute("errors",
                                                 errors)

            return Redirect.to("/board/project/new")
        }

        val project: KTable = KTable(
            name = name,
            members = mutableSetOf(user))

        tableRepository.save(project)
        tableRepository.flush()

        return Redirect.to("/board")
    }

    @GetMapping("/{token}/edit", "/{token}/edit/")
    fun renderEditProject(@PathVariable("token") token: UUID,
                          model: Model): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        if (!this.isUserProjectMember(user)) {
            return "redirect:/board/project/$token"
        }

        model["user"] = user
        model["project"] = this.project
        model["nameMaxLength"] = KTable.NAME_MAX_LENGTH

        return "Board/Project/EditProject"
    }

    @PostMapping("/edit", "/edit/")
    fun editProject(@RequestParam("token") token: UUID,
                    @RequestParam("name") name: String,
                    redirectAttributes: RedirectAttributes): RedirectView {


        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")
        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        if (!this.isUserProjectMember(user)) {
            return Redirect.to("/board/project/$token")
        }

        val errors: MutableList<String> = arrayListOf()

        if (name.isBlank()) {
            errors.add(String.format(AuthController.EMPTY_REQUIRED_FIELD_ERROR,
                                     "Name"))
        }

        if (!KTable.isNameValid(name)) {
            errors.add(KTable.INVALID_NAME_LENGTH_ERROR)
        }

        if (errors.isNotEmpty()) {
            redirectAttributes.addFlashAttribute("errors",
                                                 errors)

            return Redirect.to("/board/project/$token/edit")
        }

        this.project.name = name
        tableRepository.save(this.project)
        tableRepository.flush()

        return Redirect.to("/board/project/$token")
    }

    @GetMapping("/{token}/delete", "/{token}/delete/")
    fun renderDeleteProject(@PathVariable("token") token: UUID,
                            model: Model): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        if (!this.isUserProjectMember(user)) {
            return "redirect:/board"
        }

        model["project"] = this.project

        return "Board/Project/DeleteProject"
    }

    @PostMapping("/delete", "/delete/")
    fun deleteProject(@RequestParam("token") token: UUID): RedirectView {
        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")
        }

        val user: KUser = session.getAttribute("user") as KUser

        this.project = tableRepository.findKTableByToken(token) as KTable

        if (this.isUserProjectMember(user)) {
            tableRepository.delete(this.project)
        }

        return Redirect.to("/board")
    }

    private fun incrementNextLists(list: KList) {
        val nextLists: List<KList>
            = listRepository
                .findAllByPositionIsGreaterThanEqualAndIdIsNot(
                    list.position, list.id!!)

        for (currentList: KList in nextLists) {
            currentList.position++
            listRepository.save(currentList)
        }

        listRepository.flush()
    }

    private fun isProjectExisting(token: UUID): Boolean {
        val projectCountWithGivenToken: Int
            = tableRepository.countAllByToken(token)

        return projectCountWithGivenToken > 0
    }

    private fun isProjectHavingLists(): Boolean
        = listRepository.countAllByTable(project) > 0

    private fun isUserProjectMember(user: KUser): Boolean
        = this.project.members.any { it == user }

    private fun isListNameAlreadyTaken(name: String): Boolean
        = listRepository.countAllByName(name) > 0

}