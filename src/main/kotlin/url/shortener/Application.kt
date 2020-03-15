package url.shortener

import io.micronaut.context.event.StartupEvent
import io.micronaut.core.annotation.TypeHint
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.event.annotation.EventListener
import url.shortener.domain.UrlMap
import url.shortener.domain.UrlMapRepository
import url.shortener.domain.Visit
import url.shortener.domain.VisitRepository
import javax.inject.Singleton

@Singleton
@TypeHint(typeNames = ["org.h2.Driver", "org.h2.mvstore.db.MVTableEngine"])
class Application(
        private val urlMapRepository: UrlMapRepository,
        private val visitRepository: VisitRepository) {

    @EventListener
    fun init(event: StartupEvent) {
        val defaultSiteVisits = setOf(
                Visit(clientBrowser = "Brave", ipAddress = "127.0.0.1", urlCode = "ab3950a"),
                Visit(clientBrowser = "Chrome", ipAddress = "127.0.0.1", urlCode = "ab3950a"),
                Visit(clientBrowser = "Safari", ipAddress = "127.0.0.1", urlCode = "ab3950a")
        )
        val defaultUrlMappings = listOf(
                UrlMap(urlCode = "ab3950a", fullUrl = "https://democracynow.org", userId = "system"),
                UrlMap(urlCode = "dm40s9m", fullUrl = "http://mangaowl.com", userId = "system")
        )

        visitRepository.saveAll(defaultSiteVisits)
        urlMapRepository.saveAll(defaultUrlMappings)
    }

    companion object Main {

        @JvmStatic
        fun main(args: Array<String>) {
            Micronaut.run(*args)
        }
    }
}
