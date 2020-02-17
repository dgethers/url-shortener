package url.shortener

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import url.shortener.domain.UrlMapRepository
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.random.Random

@Controller("/")
open class UrlController(private val urlMapRepository: UrlMapRepository) {

    @Get("/{urlId}")
    fun redirectToUrl(@PathVariable urlId: String): MutableHttpResponse<URL>? {
        val result: Optional<url.shortener.domain.UrlMap> = urlMapRepository.findById(urlId)
        return if (result.isPresent) {
            val url = URI(result.get().url!!)
            HttpResponse.redirect<URL>(url)
        } else {
            HttpResponse.notFound()
        }
    }

    @Post
    @Status(HttpStatus.CREATED)
    fun addUrlMapping(@Body urlMap: url.shortener.UrlMap): String {
        val id = generateSemiUniqueId()
        val entry = url.shortener.domain.UrlMap(id, urlMap.url)
        urlMapRepository.save(entry)

        return id
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