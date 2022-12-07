package de.fuchsch.remoteview

import io.github.bonigarcia.wdm.WebDriverManager
import io.ktor.server.config.*
import io.ktor.server.engine.*
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriver

class ConfigurationTest(private val environment: ApplicationEngineEnvironment) {

    private lateinit var driver: WebDriver

    @BeforeEach
    fun setup() {
        driver = WebDriverManager.firefoxdriver().create()
    }

    @AfterEach
    fun teardown() {
        driver.quit()
    }

    @Test
    fun `configuration returns configured value`() {
        driver.get("http://127.0.0.1:8080/config.json")
        MatcherAssert.assertThat(driver.pageSource, CoreMatchers.containsString(environment.config.tryGetString("oidc.clientId")))
    }

}
