plugins {
    id("nomad.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android { namespace = "com.tranhienchuong.nomad.core.auth.firebase" }

dependencies {
    implementation(project(":core:auth"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.coroutines.play.services)
}
