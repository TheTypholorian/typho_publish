package net.typho.typho_publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.io.File

class TyphoPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.findProperty("typho_publish.website_dir")?.toString()?.let { websiteDir ->
            val websiteDir = File(websiteDir)

            project.plugins.apply("maven-publish")

            val publishing = project.extensions.getByType(PublishingExtension::class.java)
            val publication = publishing.publications.create("maven", MavenPublication::class.java)
            publication.from(project.components.getByName("java"))
            project.findProperty("typho_publish.artifact_id")?.toString()?.let { publication.artifactId = it }
            publishing.repositories.maven {
                it.name = "typho"
                it.url = websiteDir.resolve("maven").toURI()
            }

            val commitTask = project.tasks.register("commitAndPushTyphoRepository") {
                it.group = "publishing"
                it.doLast {
                    fun run(vararg args: String) {
                        val exit = ProcessBuilder(*args).directory(websiteDir).inheritIO().start().waitFor()

                        if (exit != 0) {
                            throw RuntimeException("Error running command \"${args.joinToString(separator = " ")}\", exit code $exit")
                        }
                    }

                    run("git", "pull")
                    run("git", "add", "maven/")
                    run("git", "commit", "-m", "Published ${publication.groupId}:${publication.artifactId}:${publication.version}")
                    run("git", "push")
                }
            }

            project.tasks.getByName("publishMavenPublicationToTyphoRepository") {
                it.finalizedBy(commitTask)
            }
        }
    }
}