package de.fuchsch.remoteview.extension

import de.fuchsch.remoteview.module
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.config.*
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.concurrent.thread

class KtorServerStartExtension: BeforeAllCallback {

    private var started = false

    override fun beforeAll(context: ExtensionContext?) {
        if (!started) {
            started = true
            thread {
                embeddedServer(Netty, environment = applicationEngineEnvironment {
                    connector {
                        host = "127.0.0.1"
                        port = 8080
                    }
                    module {
                        module()
                    }
                    config = MapApplicationConfig("oidc.clientId" to "oidc-test")
                }).start(wait = true)
            }
        }
    }
}
