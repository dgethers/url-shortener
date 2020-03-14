package url.shortener

import io.micronaut.context.event.StartupEvent
import io.micronaut.core.annotation.TypeHint
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.event.annotation.EventListener
import url.shortener.domain.UrlMap
import url.shortener.domain.UrlMapRepository
import javax.inject.Singleton

@Singleton
@TypeHint(typeNames = ["org.h2.Driver", "org.h2.mvstore.db.MVTableEngine"])
class Application(
        private val urlMapRepository: UrlMapRepository) {

    @EventListener
    fun init(event: StartupEvent) {
        val defaults = listOf(
                UrlMap(null, "ab3950a", "https://democracynow.org", "system"),
                UrlMap(null, "dm40s9m", "http://mangaowl.com", "system")
        )
        urlMapRepository.saveAll(defaults)
    }

    companion object Main {

        @JvmStatic
        fun main(args: Array<String>) {
            Micronaut.run(*args)
        }
    }
}
