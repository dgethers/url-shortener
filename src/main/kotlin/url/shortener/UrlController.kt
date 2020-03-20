package url.shortener

import io.micronaut.http.*
import io.micronaut.http.annotation.*
import io.micronaut.http.server.HttpServerConfiguration
import io.micronaut.http.server.util.DefaultHttpClientAddressResolver
import url.shortener.domain.UrlMap
import url.shortener.domain.UrlMapRepository
import url.shortener.domain.Visit
import url.shortener.domain.VisitRepository
import java.net.URI
import java.net.URL
import kotlin.random.Random

@Controller("/")
class UrlController(private val urlMapRepository: UrlMapRepository,
                    private val visitRepository: VisitRepository) {

    @Get("{urlCode}")
    fun redirectToUrl(request: HttpRequest<*>,
                      @PathVariable urlCode: String): MutableHttpResponse<URL>? {

        val possibleFullUrl = urlMapRepository.findFullUrlByUrlCode(urlCode)

        return if (possibleFullUrl.isPresent) {
            val clientAddressResolver = DefaultHttpClientAddressResolver(HttpServerConfiguration())
            val clientUserAgent = request.headers["User-Agent"]
            val fullUrl = possibleFullUrl.get()

            //TODO: Give default value besides blank?
            visitRepository.save(Visit(clientBrowser = clientUserAgent.orEmpty(),
                    ipAddress = clientAddressResolver.resolve(request).orEmpty(), urlCode = urlCode))
            HttpResponse.permanentRedirect(URI(fullUrl))
        } else {

            HttpResponse.notFound()
        }
    }

    @Get("visits/{urlCode}")
    fun getVisitsByUrlCode(@PathVariable urlCode: String): MutableHttpResponse<Set<Visit>> {
        val possibleUrlMap = urlMapRepository.findByUrlCode(urlCode)

        return if (possibleUrlMap.isPresent) {

            val visits = visitRepository.findByUrlCode(urlCode)
            HttpResponse.ok(visits)
        } else {

            HttpResponse.notFound()
        }
    }

    @Post
    @Consumes(MediaType.APPLICATION_JSON)
    @Status(HttpStatus.CREATED)
    fun addUrlMapping(@Body urlRequest: UrlRequest): UrlMap {
        val possibleUrlMapping = urlMapRepository.findByFullUrl(urlRequest.fullUrl)

        return if (possibleUrlMapping.isPresent) {

            possibleUrlMapping.get()
        } else {

            val urlCode = UrlRequest.generateUrlCode()
            //TODO: add check to see if url code already exists and recreate if already there
            val record = UrlMap(urlCode = urlCode, fullUrl = urlRequest.fullUrl, userId = urlRequest.userId)
            urlMapRepository.save(record)

            record
        }
    }


}

data class UrlRequest(val fullUrl: String, val userId: String) {

    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('0'..'9')

        fun generateUrlCode(): String {
            val randomString = (1..8)
                    .map { _ -> Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("");

            return randomString
        }
    }
}
