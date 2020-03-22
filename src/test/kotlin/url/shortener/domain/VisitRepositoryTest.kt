package url.shortener.domain

import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer

class VisitRepositoryTest : StringSpec() {

    private val embeddedServer = autoClose(
            ApplicationContext.run(EmbeddedServer::class.java)
    )

    private val visitRepository = embeddedServer.applicationContext.getBean(VisitRepository::class.java)

    init {
        "should insert and find visits by url code" {
            val uniqueVisits = setOf(
                    Visit(clientBrowser = "Chrome", ipAddress = "127.0.0.1", urlCode = "dm40s9m"),
                    Visit(clientBrowser = "Brave", ipAddress = "127.0.0.2", urlCode = "dm40s9m"),
                    Visit(clientBrowser = "Safari", ipAddress = "127.0.0.3", urlCode = "dm40s9m"))

            visitRepository.saveAll(uniqueVisits)

            val actual = visitRepository.findByUrlCodeOrderByIdDesc("dm40s9m")

            actual shouldContainExactlyInAnyOrder uniqueVisits
        }

        "should return an empty set when url code does not exist" {
            val actual = visitRepository.findByUrlCodeOrderByIdDesc("none")

            actual.size shouldBe 0
        }
    }

}
