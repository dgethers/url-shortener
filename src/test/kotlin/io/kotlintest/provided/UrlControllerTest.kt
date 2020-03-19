package io.kotlintest.provided

import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import url.shortener.domain.Visit

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
            val request = GET<Any>("/ab3950a")
            request.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) " +
                    "AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1")
            val response = client.toBlocking().exchange(request, HttpResponse::class.java)

            response.status shouldBe HttpStatus.PERMANENT_REDIRECT
            response.header("Location") shouldBe "https://democracynow.org"
        }

        "should return all visits by url code" {
            val actual = client.toBlocking().retrieve("/visits/ab3950a", Set::class.java)

            actual.size shouldBeGreaterThan 3
        }
    }
}
