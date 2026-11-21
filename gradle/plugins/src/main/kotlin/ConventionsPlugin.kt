import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

class ConventionsPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
            val kotlinVersion = libs.findVersion("mavencentral-kotlin").get().requiredVersion
            configurations.named { it != "ktlint" }.configureEach {
                resolutionStrategy.eachDependency {
                    if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin")) {
                        useVersion(kotlinVersion)
                    }
                }
            }
            extensions.configure(KotlinJvmProjectExtension::class.java) {
                jvmToolchain(libs.findVersion("java-compilation").get().requiredVersion.toInt())
            }
            tasks.withType(KotlinJvmCompile::class.java).configureEach {
                compilerOptions {
                    apiVersion.set(KotlinVersion.KOTLIN_2_1)
                    languageVersion.set(KotlinVersion.KOTLIN_2_1)
                }
            }
        }
    }
}
