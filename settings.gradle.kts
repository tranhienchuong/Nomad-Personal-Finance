pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Nomad"
include(":app")
include(":core:common")
include(":core:auth")
include(":core:auth-firebase")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":core:ui")
include(":feature:auth")
include(":feature:home")
include(":feature:transaction")
include(":feature:budget")
include(":feature:statistics")
include(":feature:profile")
