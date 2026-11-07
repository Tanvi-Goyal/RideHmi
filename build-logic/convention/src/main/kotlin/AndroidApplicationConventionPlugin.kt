import com.android.build.api.dsl.ApplicationExtension
import com.ridehmi.buildlogic.HmiSdk
import com.ridehmi.buildlogic.configureAndroid
import com.ridehmi.buildlogic.configureUnitTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")

        extensions.configure<ApplicationExtension> {
            configureAndroid()
            defaultConfig.targetSdk = HmiSdk.TARGET
        }
        configureUnitTests()
    }
}
