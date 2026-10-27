// Root build.gradle.kts applies Spring Boot to every subproject.
// This module contains tests only, so it does not produce a bootable JAR.
tasks.named("bootJar") { enabled = false }

dependencies {
    testImplementation("com.intuit.karate:karate-junit5:1.4.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("karate.env", System.getProperty("karate.env", "local"))
    systemProperty("api.url", System.getProperty("api.url", "http://localhost:8080"))
    outputs.upToDateWhen { false }
}
