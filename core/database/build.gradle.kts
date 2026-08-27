plugins {
    id("nomad.android.library")
}

android {
    namespace = "com.tranhienchuong.nomad.core.database"
}

dependencies {
    implementation(libs.androidx.room.runtime)
}
