import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

class PublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("maven-publish")
        pluginManager.apply("signing")

        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }
        extensions.configure<PublishingExtension> {
            with(repositories) {
                maven { maven ->
                    maven.name = "github"
                    maven.setUrl("https://maven.pkg.github.com/usefulness/issuechecker")
                    with(maven.credentials) {
                        username = "usefulness"
                        password = findConfig("GITHUB_TOKEN")
                    }
                }
                maven { maven ->
                    maven.name = "mavenCentral"
                    maven.setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                    maven.mavenContent { it.releasesOnly() }
                    with(maven.credentials) {
                        username = findConfig("OSSRH_USERNAME")
                        password = findConfig("OSSRH_PASSWORD")
                    }
                }
                maven { maven ->
                    maven.name = "mavenCentralSnapshot"
                    maven.setUrl("https://oss.sonatype.org/content/repositories/snapshots")
                    maven.mavenContent { it.snapshotsOnly() }
                    with(maven.credentials) {
                        username = findConfig("OSSRH_USERNAME")
                        password = findConfig("OSSRH_PASSWORD")
                    }
                }
            }
            with(publications) {
                register("mavenJava", MavenPublication::class.java) {
                    it.from(components.getByName("java"))
                }
            }
        }

        with(extensions.extraProperties) {
            set("signing.keyId", findConfig("SIGNING_KEY_ID"))
            set("signing.password", findConfig("SIGNING_PASSWORD"))
            set("signing.secretKeyRingFile", findConfig("SIGNING_SECRET_KEY_RING_FILE"))
        }

        extensions.configure<SigningExtension>("signing") { signing ->
            signing.sign(extensions.getByType(PublishingExtension::class.java).publications)
        }
    }

    private inline fun <reified T> ExtensionContainer.configure(crossinline receiver: T.() -> Unit) {
        configure(T::class.java) { receiver(it) }
    }
}

private fun Project.findConfig(key: String): String {
    return findProperty(key)?.toString() ?: System.getenv(key) ?: ""
}
