plugins {
    alias(libs.plugins.ridehmi.android.application)
    alias(libs.plugins.ridehmi.android.compose)
    alias(libs.plugins.ridehmi.hilt)
}

android {
    namespace = "com.ridehmi"

    defaultConfig {
        applicationId = "com.ridehmi"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
        }
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:dashboard:ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
}
