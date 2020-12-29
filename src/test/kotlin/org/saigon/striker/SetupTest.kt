package org.saigon.striker

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.KofuApplication

open class SetupTest(private val application: KofuApplication) {

    lateinit var context: ConfigurableApplicationContext

    @BeforeAll
    fun beforeAll() {
        context = application.run(profiles = Profiles.TEST)
    }

    @AfterAll
    fun afterAll() {
        context.close()
    }
}
