dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
    }
}

rootProject.name = "task-management"

include(
    "api-gateway",
    "auth-service",
    "task-service",
    "integration-tests"
)