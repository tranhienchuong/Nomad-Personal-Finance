plugins {
    id("nomad.android.library")
}

android {
    namespace = "com.tranhienchuong.nomad.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
}
