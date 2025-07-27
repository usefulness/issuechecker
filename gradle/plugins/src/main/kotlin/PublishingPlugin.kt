import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTask

@Suppress("unused")
class PublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.vanniktech.maven.publish")
        pluginManager.apply("org.jetbrains.dokka")

        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            tasks.named("processResources", ProcessResources::class.java) {
                from(rootProject.file("LICENSE"))
            }
        }

        extensions.configure(MavenPublishBaseExtension::class.java) {
            publishToMavenCentral()
            coordinates(group.toString(), name, version.toString())

            signAllPublications()

            configureBasedOnAppliedPlugins()

            pom {
                name.set("${project.group}:${project.name}")
                description.set("A tool that scans sources for all resolved links to public trackers")
                url.set("https://github.com/usefulness/issuechecker")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/usefulness/issuechecker/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("mateuszkwiecinski")
                        name.set("Mateusz Kwiecinski")
                        email.set("36954793+mateuszkwiecinski@users.noreply.github.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/usefulness/issuechecker.git")
                    developerConnection.set("scm:git:ssh://github.com/usefulness/issuechecker.git")
                    url.set("https://github.com/usefulness/issuechecker/tree/master")
                }
            }
        }
        extensions.configure(PublishingExtension::class.java) {
            repositories {
                maven {
                    name = "github"
                    setUrl("https://maven.pkg.github.com/usefulness/issuechecker")
                    credentials {
                        username = "usefulness"
                        password = findConfig("GITHUB_TOKEN")
                    }
                }
            }
        }
    }
}

private fun Project.findConfig(key: String): String {
    return findProperty(key)?.toString() ?: System.getenv(key) ?: ""
}
