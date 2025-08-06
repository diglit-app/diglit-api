plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spotless)
}

group = "app.diglit.api"
version = file("version").readLines().first()
description = "Server-side REST API for the Digital Literacy Driver License App"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.ktor.core)
    implementation(libs.bundles.ktor.serialization)
    implementation(libs.bundles.ktor.auth)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.jdbc)
    implementation(libs.bcrypt)
    implementation(libs.dotenv)
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.mockito)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

application {
    mainClass.set("app.diglit.api.MainKt")
}

spotless {
    kotlin {
        ktlint(libs.versions.ktlint.get())

        // Since spotless cannot find the files in a multiplatform project, we have to specify the source sets manually
        target("**/*.kt")

        // Convert text to Kotlin multiline comment
        val license = "/*\n${file("LICENSE_HEADER").readLines().joinToString("\n") { " * $it" }}\n*/"
        licenseHeader(license)
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    wrapper {
        gradleVersion = libs.versions.gradle.get()
        distributionType = Wrapper.DistributionType.ALL
    }
}
