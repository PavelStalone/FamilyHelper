plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "FamilyHelper"

include(":app")
include(":search-activity")
include(":core:data")
include(":core:common")
include(":core:ai")

include(":calendar")
include("family")
include(":auth")