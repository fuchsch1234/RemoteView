package de.fuchsch.remoteview

import io.github.bonigarcia.wdm.WebDriverManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriver

class TitleTest {

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
    fun `title is set`() {
        driver.get("http://127.0.0.1:8080")
        assertThat(driver.title, containsString("Ktor"))
    }

}
