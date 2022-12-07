package de.fuchsch.remoteview.extension

import de.fuchsch.remoteview.module
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.config.*
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

class KtorServerStartExtension: BeforeAllCallback, ParameterResolver {

    private var started = false

    private val serverStartedSemaphore = Semaphore(0)

    companion object {
        private val environment = applicationEngineEnvironment {
            connector {
                host = "127.0.0.1"
                port = 8080
            }
            module {
                module()
            }
            config = MapApplicationConfig("oidc.clientId" to "oidc-test")
        }
    }

    override fun beforeAll(context: ExtensionContext?) {
        if (!started) {
            started = true
            thread {
                embeddedServer(Netty, environment = environment) {
                    environment.monitor.subscribe(ApplicationStarted) {
                        serverStartedSemaphore.release()
                    }
                }.start(wait = true)
            }
            serverStartedSemaphore.acquire()
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?) =
        parameterContext?.parameter?.type == ApplicationEngineEnvironment::class.java

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?) =
        environment
}
