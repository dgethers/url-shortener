package url.shortener.domain

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.H2)
interface VisitRepository : CrudRepository<Visit, Long> {

    fun findByUrlCode(urlCode: String): Set<Visit>
}