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
                Visit(clientBrowser = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36", ipAddress = "127.0.0.1", urlCode = "ab3950a"),
                Visit(clientBrowser = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36", ipAddress = "127.0.0.1", urlCode = "ab3950a"),
                Visit(clientBrowser = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0", ipAddress = "127.0.0.1", urlCode = "ab3950a"),
                Visit(clientBrowser = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1", ipAddress = "127.0.0.1", urlCode = "ab3950a")
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
