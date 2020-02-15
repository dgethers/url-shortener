package url.shortener

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import java.net.URI
import java.net.URL
import kotlin.random.Random

@Controller("/")
class UrlController {

    @Get("/{urlId}")
    fun redirectToUrl(@PathVariable urlId: String): MutableHttpResponse<URL>? {
        if (urlMapping.containsKey(urlId)) {
            val url = URI(urlMapping[urlId]!!)
            return HttpResponse.redirect<URL>(url)
        } else {
            return HttpResponse.notFound()
        }
    }

    @Post
    @Status(HttpStatus.CREATED)
    fun addUrlMapping(@Body urlMap: url.shortener.UrlMap): url.shortener.UrlMap {
        val id = generateSemiUniqueId()
        val entry = UrlMap(id, urlMap.url)
        urlMapping[id] = entry.url

        return entry
    }

    companion object UrlMap {
        val urlMapping = mutableMapOf(
                "ab3950a" to "https://democracynow.org",
                "dm40s9m" to "http://mangaowl.com/"
        )
    }
}

data class UrlMap(val id: String, val url: String)

fun generateSemiUniqueId(): String {
    val charPool: List<Char> = ('a'..'z') + ('0'..'9')
    val randomString = (1..8)
            .map { _ -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

    return randomString
}