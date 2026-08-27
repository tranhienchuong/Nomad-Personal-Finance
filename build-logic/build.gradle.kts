plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "nomad.android.library"
            implementationClass = "NomadAndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "nomad.android.compose"
            implementationClass = "NomadAndroidComposeConventionPlugin"
        }
    }
}
