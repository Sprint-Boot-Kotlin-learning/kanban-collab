package belicfr.exercises.kanbancollab.controllers.board

import belicfr.exercises.kanbancollab.controllers.Middleware
import belicfr.exercises.kanbancollab.controllers.auth.AuthController
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
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView
import java.util.Optional
import java.util.UUID

@Controller
@RequestMapping("/board/project/{token}/{listId}")
class ListController(private val listRepository: ListRepository,
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

    @GetMapping("/move/right", "/move/right/")
    fun moveToRight(@PathVariable("token") token: UUID,
                   @PathVariable("listId") listId: Long): RedirectView {

        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")
        }

        this.project = tableRepository.findKTableByToken(token) as KTable

        val listQuery: Optional<KList> = listRepository.findById(listId)
        val lastPositionQuery: KList?
            = listRepository.findFirstByTableOrderByPositionDesc(this.project)
        val lastPosition: Int
        val user: KUser = session.getAttribute("user") as KUser

        if (lastPositionQuery !== null) {
            lastPosition = lastPositionQuery.position
        } else {
            lastPosition = listQuery.get().position
        }

        if (listQuery.isPresent
            && listQuery.get().position < lastPosition
            && this.isUserProjectMember(user)) {

            val list: KList = listQuery.get()
            val nextListQuery: KList? = listRepository.findKListByPosition(
                list.position + 1)

            list.position++
            listRepository.save(list)

            if (nextListQuery !== null) {
                val nextList: KList = nextListQuery

                nextList.position--
                listRepository.save(nextList)
            }

            listRepository.flush()
        }

        return Redirect.to("/board/project/$token")
    }

    @GetMapping("/edit", "/edit/")
    fun renderEditList(@PathVariable("token") token: UUID,
                       @PathVariable("listId") listId: Long,
                       model: Model): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        this.project = tableRepository.findKTableByToken(token) as KTable

        val list: Optional<KList> = listRepository.findById(listId)

        if (!list.isPresent || !this.isListExisting(listId)) {
            return "redirect:/board/project/$token"
        }

        model["project"] = this.project
        model["list"] = list.get()
        model["nameMaxLength"] = KList.NAME_MAX_LENGTH

        return "Board/Project/Lists/EditList"
    }

    @PostMapping("/edit", "/edit/")
    fun editList(@RequestParam("token") token: UUID,
                 @RequestParam("listId") listId: Long,
                 @RequestParam("name") name: String,
                 redirectAttributes: RedirectAttributes): RedirectView {

        val errors: MutableList<String> = arrayListOf()
        val user: KUser = session.getAttribute("user") as KUser

        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")
        }

        if (!this.isListExisting(listId)
            || !this.isUserProjectMember(user)) {

            return Redirect.to("/board/project/$token")
        }

        val list: KList = listRepository.findById(listId).get()

        if (name.isBlank()) {
            errors.add(String.format(AuthController.EMPTY_REQUIRED_FIELD_ERROR,
                                     "Name"))
        }

        if (!KList.isNameValid(name)) {
            errors.add(KList.INVALID_NAME_LENGTH_ERROR)
        }

        if (name == list.name) {
            return Redirect.to("/board/project/$token")
        }

        if (this.isListNameAlreadyTaken(name)) {
            errors.add(KList.NAME_ALREADY_TAKEN_ERROR)
        }

        if (errors.isNotEmpty()) {
            redirectAttributes.addFlashAttribute("errors",
                                                 errors)

            return Redirect.to("/board/project/$token/$listId/edit")
        }


        list.name = name
        listRepository.save(list)
        listRepository.flush()

        return Redirect.to("/board/project/$token")
    }

    @GetMapping("/delete", "/delete/")
    fun renderDeleteList(@PathVariable("token") token: UUID,
                         @PathVariable("listId") listId: Long,
                         model: Model): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        this.project = tableRepository.findKTableByToken(token) as KTable

        val user: KUser = session.getAttribute("user") as KUser

        if (!this.isUserProjectMember(user)) {
            return "redirect:/board/project/$token"
        }

        val list: Optional<KList> = listRepository.findById(listId)

        if (!list.isPresent || !this.isListExisting(listId)) {
            return "redirect:/board/project/$token"
        }

        model["project"] = this.project
        model["list"] = list.get()

        return "Board/Project/Lists/DeleteList"
    }

    @PostMapping("/delete", "/delete/")
    fun deleteList(@RequestParam("token") token: UUID,
                   @RequestParam("listId") listId: Long): RedirectView {

        val user: KUser = session.getAttribute("user") as KUser

        if (!this.isProjectExisting(token)) {
            return Redirect.to("/board")
        }

        this.project = tableRepository.findKTableByToken(token) as KTable

        val list: Optional<KList> = listRepository.findById(listId)

        if (this.isListExisting(listId)
            && list.isPresent
            && this.isUserProjectMember(user)) {

            val rightLists: List<KList>
                = listRepository.findAllByPositionIsGreaterThanEqualAndIdIsNot(
                    list.get().position, list.get().id!!)

            for (rightList: KList in rightLists) {
                rightList.position--
                listRepository.save(rightList)
            }

            this.project.lists.remove(list.get())
            tableRepository.save(this.project)
            tableRepository.flush()

            listRepository.delete(list.get())
            listRepository.flush()
        }

        return Redirect.to("/board/project/$token")
    }

    @GetMapping("/cards/create", "/cards/create/")
    fun renderCreateCard(@PathVariable("token") token: UUID,
                         @PathVariable("listId") listId: Long,
                         model: Model): String {

        if (!this.isProjectExisting(token)) {
            return "redirect:/board"
        }

        this.project = tableRepository.findKTableByToken(token) as KTable

        val user: KUser = session.getAttribute("user") as KUser

        if (!this.isUserProjectMember(user)) {
            return "redirect:/board/project/$token"
        }

        val list: Optional<KList> = listRepository.findById(listId)

        if (!list.isPresent || !this.isListExisting(listId)) {
            return "/board/project/$token"
        }

        model["projectToken"] = token
        model["list"] = list.get()

        return "Board/Project/Lists/Cards/NewCard"
    }

    private fun isProjectExisting(token: UUID): Boolean {
        val projectCountWithGivenToken: Int
            = tableRepository.countAllByToken(token)

        return projectCountWithGivenToken > 0
    }

    private fun isUserProjectMember(user: KUser): Boolean
        = this.project.members.any { it == user }

    private fun isListExisting(listId: Long): Boolean
        = this.project.lists.any { it.id == listId }

    private fun isListNameAlreadyTaken(name: String): Boolean
        = listRepository.countAllByName(name) > 0

}