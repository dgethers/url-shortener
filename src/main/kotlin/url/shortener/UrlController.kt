package url.shortener

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import url.shortener.domain.UrlMap
import url.shortener.domain.UrlMapRepository
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.random.Random

@Controller("/")
open class UrlController(private val urlMapRepository: UrlMapRepository) {

    @Get("{urlCode}")
    fun redirectToUrl(@PathVariable urlCode: String): MutableHttpResponse<URL>? {
        val result: Optional<UrlMap> = urlMapRepository.findByUrlCode(urlCode)

        return if (result.isPresent) {

            val record: UrlMap = result.get()
            val url = URI(record.fullUrl)

            HttpResponse.permanentRedirect(url)
        } else {

            HttpResponse.notFound()
        }
    }

    @Get("stats/{urlCode}")
    fun getStatsForUrlByCode(@PathVariable urlCode: String): MutableHttpResponse<UrlMap>? {
        val result = urlMapRepository.findByUrlCode(urlCode)

        return if (result.isPresent) {

            HttpResponse.ok(result.get())
        } else {

            HttpResponse.notFound()
        }
    }

    @Post
    @Status(HttpStatus.CREATED)
    fun addUrlMapping(@Body urlRequest: UrlRequest): UrlMap {
        val result = urlMapRepository.findByUrlCode(urlRequest.urlCode)

        return if (result.isPresent) {

            result.get()
        } else {
            val id = UrlRequest.generateSemiUniqueId() //TODO: Rename
            val entry = UrlMap(urlCode = id, fullUrl = urlRequest.urlCode, userId = urlRequest.userId)
            urlMapRepository.save(entry)

            entry
        }
    }


}

data class UrlRequest(val urlCode: String, val userId: String) {

    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('0'..'9')

        fun generateSemiUniqueId(): String {
            val randomString = (1..8)
                    .map { _ -> Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("");

            return randomString
        }
    }
}
