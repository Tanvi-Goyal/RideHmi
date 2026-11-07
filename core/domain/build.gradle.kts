plugins {
    alias(libs.plugins.ridehmi.jvm.library)
}

dependencies {
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)
    api(libs.javax.inject)
}
