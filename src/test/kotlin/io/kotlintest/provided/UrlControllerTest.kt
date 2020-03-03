package io.kotlintest.provided

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer

class UrlControllerTest : StringSpec() {

    private val embeddedServer = autoClose(
            ApplicationContext.run(EmbeddedServer::class.java)
    )

    val client = autoClose(
            embeddedServer.applicationContext.createBean(RxHttpClient::class.java, embeddedServer.getURL())
    )

    init {
        "should return 404 when unique id is not found" {
            val e = shouldThrow<HttpClientResponseException> { client.toBlocking().exchange<Any>("/issues") }

            e.status shouldBe HttpStatus.NOT_FOUND
        }

        "should return 302 redirect when unique id is found" {
            //TODO: Implement test for this or get rid of it
        }

        "should return url stats by an id" {
            //TODO: Implement test for this or get rid of it
        }
    }
}
