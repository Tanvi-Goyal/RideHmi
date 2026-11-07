package com.ridehmi.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

object HmiSdk {
    const val COMPILE = 37
    const val MIN = 26
    const val TARGET = 37
}

internal fun CommonExtension.configureAndroid() {
    compileSdk {
        version = release(HmiSdk.COMPILE)
    }
    defaultConfig.minSdk = HmiSdk.MIN
    defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    compileOptions.sourceCompatibility = JavaVersion.VERSION_17
    compileOptions.targetCompatibility = JavaVersion.VERSION_17
}
