package belicfr.exercises.kanbancollab

import belicfr.exercises.kanbancollab.models.KCard
import belicfr.exercises.kanbancollab.models.KList
import belicfr.exercises.kanbancollab.models.KTable
import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.CardRepository
import belicfr.exercises.kanbancollab.models.repositories.ListRepository
import belicfr.exercises.kanbancollab.models.repositories.TableRepository
import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

    @Bean
    fun databaseInitializer(userRepository: UserRepository,
                            cardRepository: CardRepository,
                            listRepository: ListRepository,
                            tableRepository: TableRepository)
        = ApplicationRunner {

        val user1: KUser = KUser(
            firstname = "Jonathan",
            lastname = "LASTNAME",
            login = KUser.Username("login"),
            password = KUser.Password("passwordpassword"),
            email = KUser.EmailAddress("test@dev.fr")
        )

        val user2: KUser = KUser(
            firstname = "Jonathan",
            lastname = "LeNom",
            login = KUser.Username("login22"),
            password = KUser.Password("password"),
            email = KUser.EmailAddress("email@example.com")
        )

        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.flush()

        val table1: KTable = KTable(
            name = "Project table",
            members = mutableSetOf(user1, user2))

        tableRepository.save(table1)

        val list1: KList = KList(
            name = "Bugs",
            position = 1,
            table = table1)

        listRepository.save(list1)

        table1.lists.add(list1)
        tableRepository.save(table1)

        cardRepository.save(KCard(
            title = "Fix bugs on app1",
            description = "There are many bugs on app1!!...",
            contributors = mutableSetOf(),
            list = list1
        ))

        listRepository.flush()
        cardRepository.flush()
        tableRepository.flush()
    }
}