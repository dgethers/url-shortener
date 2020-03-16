package url.shortener

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import url.shortener.domain.UrlMap
import url.shortener.domain.UrlMapRepository
import url.shortener.domain.Visit
import url.shortener.domain.VisitRepository
import java.net.URI
import java.net.URL
import kotlin.random.Random

@Controller("/")
open class UrlController(private val urlMapRepository: UrlMapRepository,
                         private val visitRepository: VisitRepository) {

    @Get("{urlCode}")
    fun redirectToUrl(@Header("X-Forwarded-For") clientIpAddress: String?,
                      @Header("User-Agent") clientUserAgent: String?,
                      @PathVariable urlCode: String): MutableHttpResponse<URL>? {

        val possibleFullUrl = urlMapRepository.findFullUrlByUrlCode(urlCode)

        return if (possibleFullUrl.isPresent) {

            val fullUrl = possibleFullUrl.get()
            //TODO: Give default value of undefined
            visitRepository.save(Visit(clientBrowser = clientUserAgent.orEmpty(), ipAddress = clientIpAddress.orEmpty(), urlCode = urlCode))
            HttpResponse.permanentRedirect(URI(fullUrl))
        } else {

            HttpResponse.notFound()
        }
    }

    @Get("stats/{urlCode}")
    fun getStatsForUrlCode(@PathVariable urlCode: String): MutableHttpResponse<UrlStatResponse>? {
        val possibleUrlMap = urlMapRepository.findByUrlCode(urlCode)

        return if (possibleUrlMap.isPresent) {

            val urlMap = possibleUrlMap.get()
            val visits = visitRepository.findByUrlCode(urlCode)
            HttpResponse.ok(UrlStatResponse(urlMap, visits))
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

data class UrlStatResponse(val urlMap: UrlMap, val visits: Set<Visit>)

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
