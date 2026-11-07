plugins {
    alias(libs.plugins.ridehmi.android.library)
    alias(libs.plugins.ridehmi.hilt)
}

android {
    namespace = "com.ridehmi.core.data"
}

dependencies {
    api(project(":core:domain"))
}
