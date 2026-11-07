import com.ridehmi.buildlogic.libs
import com.ridehmi.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("ridehmi.android.library")
        pluginManager.apply("ridehmi.android.compose")
        pluginManager.apply("ridehmi.hilt")

        dependencies {
            add("implementation", project(":core:designsystem"))
            add("implementation", project(":core:domain"))

            add("implementation", libs.library("androidx-core-ktx"))
            add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
            add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
            add("implementation", libs.library("androidx-hilt-lifecycle-viewmodel-compose"))
        }
    }
}
