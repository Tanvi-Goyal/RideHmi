package com.ridehmi.buildlogic

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

private const val TESTING_MODULE = ":core:testing"

internal fun Project.configureUnitTests() {
    dependencies {
        add("testImplementation",
            libs.library("junit"))
        add("testImplementation",
            libs.library("kotlin-test"))
        add("testImplementation",
            libs.library("kotlinx-coroutines-test"))
        add("testImplementation",
            libs.library("turbine"))
        if (path != TESTING_MODULE) {
            add("testImplementation",
                project(TESTING_MODULE))
        }
    }
}
