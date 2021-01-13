package com.pysun.flutter

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishFlutterAarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def mavenExt = project.extensions.create("mavenSupport", MavenExtension)
        project.gradle.projectsEvaluated {

            project.rootProject.subprojects.findAll { it.name != "app" }.each { subProject ->
                subProject.tasks.findByName("uploadArchives").doFirst {
                    assert subProject.hasProperty("output-dir")

                    File outRepoDir = new File("${subProject.property("output-dir")}${File.separator}outputs${File.separator}repo")
                    if (!outRepoDir.exists()) {
                            outRepoDir.mkdirs()
                        }
                    if (mavenExt.useRemote) {

                        subProject.uploadArchives.repositories.mavenDeployer.repository(url: mavenExt.remoteUrl) {
                            authentication(userName: mavenExt.userName, password: mavenExt.password)
                        }
                    } else {

                        if (mavenExt.localUrl?.trim()) {
                            File outLocalDir = new File("${mavenExt.localUrl}")
                            if (!outLocalDir.exists()) {
                                outLocalDir.mkdirs()
                            }
                            subProject.uploadArchives.repositories.mavenDeployer.repository(url: mavenExt.localUrl)

                        }


                    }


                    if (subProject.name.equalsIgnoreCase("flutter")) {
                        subProject.uploadArchives.repositories.mavenDeployer.pom {
                            artifactId = (subProject.uploadArchives.repositories.mavenDeployer.pom.artifactId).replace("flutter", mavenExt.pomArtifactId ?: "flutter")
                            groupId = mavenExt.pomGroupId?:subProject.uploadArchives.repositories.mavenDeployer.pom.groupId

//                            version = mavenExt.pomVersion ?: subProject.uploadArchives.repositories.mavenDeployer.pom.version

                        }

                    }

                    //暂时无法弄清如何修改依赖插件版本
//                    subProject.uploadArchives.repositories.mavenDeployer.pom {
//                        version = mavenExt.pomVersion ?: subProject.uploadArchives.repositories.mavenDeployer.pom.version
//
//                    }


                }
            }
        }


    }
}
