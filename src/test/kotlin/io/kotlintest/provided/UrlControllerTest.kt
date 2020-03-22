package io.kotlintest.provided

import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
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

        "should add url mapping when url code does not exist" {
            val request = POST("/", "{ \"fullUrl\": \"https://sailormoon.fandom.com/wiki/Sailor_Moon_Wiki\"," +
                    "\"userId\" : \"test\" }")

            val actual = client.toBlocking().retrieve(request, UrlMap::class.java)
            actual.fullUrl shouldBe "https://sailormoon.fandom.com/wiki/Sailor_Moon_Wiki"
            actual.userId shouldBe "test"
        }

        "should not create new url mapping if full url already exists" {
            val request = POST("/", "{ \"fullUrl\": \"https://democracynow.org\"," +
                    "\"userId\" : \"test\" }")

            val actual = client.toBlocking().retrieve(request, UrlMap::class.java)
            actual.fullUrl shouldBe "https://democracynow.org"
            actual.urlCode shouldBe "ab3950a"
            actual.userId shouldNotBe "test"
        }

        "should delete url mapping if url code exists" {
            val request = DELETE<Any>("/dm40s9m")

            val actual = client.toBlocking().exchange(request, HttpStatus::class.java)
            actual.status() shouldBe HttpStatus.NO_CONTENT
        }

        "should return not found if url code doesn't exist when deleting" {
            val request = DELETE<Any>("/does_not_exist")
            val e = shouldThrow<HttpClientResponseException> { client.toBlocking().exchange<Any, Any>(request) }

            e.status shouldBe HttpStatus.NOT_FOUND
        }

        "should return url mappings by user" {
            val actual = client.toBlocking().retrieve("/mappings/test", Set::class.java)

            actual.size shouldBeGreaterThanOrEqual 1
        }
    }
}
