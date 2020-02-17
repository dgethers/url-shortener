package url.shortener.domain

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class UrlMap(@Id var id: String? = null,
                  var url: String? = null)