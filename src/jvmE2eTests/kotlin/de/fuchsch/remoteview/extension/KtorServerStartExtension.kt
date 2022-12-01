package de.fuchsch.remoteview.extension

import de.fuchsch.remoteview.module
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.concurrent.thread

class KtorServerStartExtension: BeforeAllCallback {

    private var started = false

    override fun beforeAll(context: ExtensionContext?) {
        if (!started) {
            started = true
            thread {
                embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
                    module()
                }.start(wait = true)
            }
        }
    }
}
