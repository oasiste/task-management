plugins {
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
    tasks.withType<Test> { useJUnitPlatform() }
}
