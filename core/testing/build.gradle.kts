plugins {
    alias(libs.plugins.ridehmi.jvm.library)
}

dependencies {
    api(project(":core:domain"))
    api(libs.junit)
    api(libs.kotlin.test)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
}
