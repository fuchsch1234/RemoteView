package de.fuchsch.remoteview

import de.fuchsch.remoteview.dto.Configuration
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/RemoteView.js") {}
    }
}

fun Application.module() {
    val clientId = environment.config.propertyOrNull("oidc.clientId")?.getString() ?: "ClientId missing"
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        get("/config.json") {
            val configuration = Configuration(clientId)
            call.respond(configuration)
        }
        static("/static") {
            resources()
        }
    }
}
