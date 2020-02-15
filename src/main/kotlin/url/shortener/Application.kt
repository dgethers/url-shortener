package url.shortener

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("url.shortener")
                .mainClass(Application.javaClass)
                .start()
    }
}