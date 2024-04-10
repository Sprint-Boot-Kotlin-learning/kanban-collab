package belicfr.exercises.kanbancollab

import belicfr.exercises.kanbancollab.models.KUser
import belicfr.exercises.kanbancollab.models.repositories.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

    @Bean
    fun databaseInitializer(userRepository: UserRepository)
        = ApplicationRunner {

        userRepository.save(KUser(
            id = 1L,
            firstname = "Jonathan",
            lastname = "LASTNAME",
            login = KUser.Username("login"),
            password = KUser.Password("passwordpassword"),
            email = KUser.EmailAddress("test@dev.fr")
        ))
    }
}