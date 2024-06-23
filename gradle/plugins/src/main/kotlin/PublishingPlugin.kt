import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTask

@Suppress("unused")
class PublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("maven-publish")
        if (findConfig("SIGNING_PASSWORD").isNotEmpty()) {
            pluginManager.apply("signing")
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            pluginManager.apply("org.jetbrains.dokka")

            tasks.withType(DokkaTask::class.java).configureEach {
                notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/1217")
            }

            tasks.register("javadocJar", Jar::class.java) {
                archiveClassifier.set("javadoc")
                from(tasks.named("dokkaJavadoc"))
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
                maven {
                    name = "mavenCentral"
                    setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                    mavenContent { releasesOnly() }
                    credentials {
                        username = findConfig("OSSRH_USERNAME")
                        password = findConfig("OSSRH_PASSWORD")
                    }
                }
                maven {
                    name = "mavenCentralSnapshot"
                    setUrl("https://oss.sonatype.org/content/repositories/snapshots")
                    mavenContent { snapshotsOnly() }
                    credentials {
                        username = findConfig("OSSRH_USERNAME")
                        password = findConfig("OSSRH_PASSWORD")
                    }
                }
            }
            publications.named { it == "jvm" }.configureEach {
                this as MavenPublication
                artifact(tasks.named("javadocJar"))
            }
            publications.configureEach {
                if (this !is MavenPublication) {
                    return@configureEach
                }

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
        }
        pluginManager.withPlugin("signing") {
            with(extensions.extraProperties) {
                set("signing.keyId", findConfig("SIGNING_KEY_ID"))
                set("signing.password", findConfig("SIGNING_PASSWORD"))
                set("signing.secretKeyRingFile", findConfig("SIGNING_SECRET_KEY_RING_FILE"))
            }

            extensions.configure<SigningExtension>("signing") {
                sign(extensions.getByType(PublishingExtension::class.java).publications)
            }
        }
    }
}

private fun Project.findConfig(key: String): String {
    return findProperty(key)?.toString() ?: System.getenv(key) ?: ""
}
