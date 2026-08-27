plugins {
    id("nomad.android.library")
    id("nomad.android.compose")
}

android { namespace = "com.tranhienchuong.nomad.feature.transaction" }

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
}
