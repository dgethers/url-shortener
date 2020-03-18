package io.kotlintest.provided

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import url.shortener.UrlStatResponse
import url.shortener.domain.UrlMap

@MicronautTest
class UrlControllerTest : StringSpec() {

    private val embeddedServer = autoClose(
            ApplicationContext.run(EmbeddedServer::class.java)
    )

    private val client = autoClose(
            embeddedServer.applicationContext.createBean(RxHttpClient::class.java, embeddedServer.getURL())
    )

    init {
        "should return 404 when unique url code is not found" {
            val e = shouldThrow<HttpClientResponseException> { client.toBlocking().exchange<Any>("/issues") }

            e.status shouldBe HttpStatus.NOT_FOUND
        }

        "should return 308 permanent redirect when url code is found" {
            val response = client.toBlocking().exchange<HttpResponse<HttpStatus>>("/ab3950a")

            response.status shouldBe HttpStatus.PERMANENT_REDIRECT
            response.header("Location") shouldBe "https://democracynow.org"
        }

        "should return url stats by an id" {
            val actual = client.toBlocking().retrieve("/stats/ab3950a", UrlStatResponse::class.java)

            //TODO: Add check for visits
            actual.urlMap.urlCode shouldBe "ab3950a"
            actual.urlMap.fullUrl shouldBe "https://democracynow.org"
            actual.urlMap.userId shouldBe "system"
        }
    }
}
