package url.shortener.domain

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.util.*

@JdbcRepository(dialect = Dialect.H2)
interface UrlMapRepository : CrudRepository<UrlMap, String> {

    fun findByUrl(url: String): Optional<UrlMap>
}
