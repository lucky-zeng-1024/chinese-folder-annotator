plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.github.chinesename"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

// See https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    type.set("IC") // Target IDE Platform
    val lp = providers.gradleProperty("intellij.localPath").orNull
    if (lp != null && lp.isNotBlank()) {
        println("Using local IntelliJ at: $lp")
        localPath.set(lp)
    } else {
        version.set("2023.1")
    }
    plugins.set(listOf(/* Plugin Dependencies */))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>""".trimIndent())
    }
    // Avoid launching IDE to collect searchable options in headless/CI or with 2025.2
    buildSearchableOptions { enabled = false }
    jarSearchableOptions { enabled = false }
}

