package url.shortener.domain

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.H2)
interface UrlMapRepository : CrudRepository<UrlMap, Long> {

    fun findByUrlCode(urlCode: String): Optional<UrlMap>
    fun findByFullUrl(fullUrl: String): Optional<UrlMap>
    fun findFullUrlByUrlCode(urlCode: String): Optional<String>
    fun findByUserId(userId: String): Set<UrlMap>
}
