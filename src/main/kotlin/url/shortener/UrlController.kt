package url.shortener

import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import java.net.URI
import java.net.URL

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

     companion object UrlMap {
        val urlMapping = mapOf(
                "ab3950a" to "https://democracynow.org",
                "dm40s9m" to "http://mangaowl.com/"
        )
    }
}