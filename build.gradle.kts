plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

subprojects {
    apply {
        plugin("java")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
        maven { url = uri("https://jitpack.io") }
    }

    configurations.configureEach {
        // Check for updates every sync
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}

var libProjects = mutableSetOf(
    project(":multiview:core"),
    project(":extensions:imgui-window"),
)

configure(libProjects) {
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    group = LibExt.groupId
    version = LibExt.libVersion

    publishing {
        repositories {
            maven {
                url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://central.sonatype.com/repository/maven-snapshots/")
                } else {
                    uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                }
                credentials {
                    username = System.getenv("CENTRAL_PORTAL_USERNAME")
                    password = System.getenv("CENTRAL_PORTAL_PASSWORD")
                }
            }
        }
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing.publications.configureEach {
        if (this is MavenPublication) {
            pom {
                name.set("gdx-multi-view")
                description.set("Render libgdx application in the framebuffer")
                url.set("https://github.com/xpenatan/gdx-multi-view")
                developers {
                    developer {
                        id.set("Xpe")
                        name.set("Natan")
                    }
                }
                scm {
                    connection.set("scm:git:git://https://github.com/xpenatan/gdx-multi-view.git")
                    developerConnection.set("scm:git:ssh://https://github.com/xpenatan/gdx-multi-view.git")
                    url.set("http://https://github.com/xpenatan/gdx-multi-view/tree/master")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }

    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if(signingKey != null && signingPassword != null) {
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            publishing.publications.configureEach {
                sign(this)
            }
        }
    }
}