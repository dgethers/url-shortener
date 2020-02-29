package url.shortener.domain

import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer

class UrlMapRepositoryTest : StringSpec() {

    private val embeddedServer = autoClose(
            ApplicationContext.run(EmbeddedServer::class.java)
    )

    private val urlMapRepository = embeddedServer.applicationContext.getBean(UrlMapRepository::class.java)

    init {
        "should find url inserted" {
            val actual = urlMapRepository.findById("ab3950a")
            actual.isPresent shouldBe true
        }

        "should not find url that has not been inserted" {
            val actual = urlMapRepository.findById("does_not_exist")
            actual.isPresent shouldBe false
        }

        "should find by url when present" {
            val actual = urlMapRepository.findByUrl("https://democracynow.org")
            actual.isPresent shouldBe true
        }

        "should update visit count by 1" {
            val recordToBeUpdated = urlMapRepository.findById("ab3950a").get()
            recordToBeUpdated.visits += 1
            urlMapRepository.update(recordToBeUpdated)
            val actualVisits = urlMapRepository.findById("ab3950a").get().visits
            actualVisits shouldBeGreaterThan 0
        }
    }

}
