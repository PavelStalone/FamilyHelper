package rut.uvp.app.test

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import rut.uvp.app.config.TestConfig

@Component
@Qualifier(TestConfig.TEST)
class TestData {

    var familyId: String = ""
    var querySystemPrompt = ""
    var parseSystemPrompt = ""
}
